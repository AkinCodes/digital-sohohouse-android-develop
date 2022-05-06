package com.sohohouse.seven.common.interactors


import android.annotation.SuppressLint
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.sohohouse.seven.common.dagger.appComponent
import com.sohohouse.seven.common.extensions.accessibleVenuesIds
import com.sohohouse.seven.common.extensions.isOpenNow
import com.sohohouse.seven.common.extensions.isVisiting
import com.sohohouse.seven.common.house.HouseType
import com.sohohouse.seven.common.prefs.VenueWorker
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.user.UserSessionManager
import com.sohohouse.seven.fcm.FirebaseRegistrationService
import com.sohohouse.seven.network.auth.AuthenticationRequestFactory
import com.sohohouse.seven.network.auth.model.LoginResponse
import com.sohohouse.seven.network.auth.request.ChangePasswordRequest
import com.sohohouse.seven.network.auth.request.LoginRequest
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.models.*
import com.sohohouse.seven.network.core.request.GetAccountRequest
import com.sohohouse.seven.network.core.request.PostAccountVerificationLinkRequest
import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineDispatcher
import moe.banana.jsonapi2.HasOne
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AccountInteractor @Inject constructor(
    private val authenticationRequestFactory: AuthenticationRequestFactory,
    private val zipRequestsUtil: ZipRequestsUtil,
    private val userSessionManager: UserSessionManager,
    private val userManager: UserManager,
    private val firebaseRegistrationService: FirebaseRegistrationService,
    private val workManager: WorkManager,
    private val sohoApiService: SohoApiService,
    private val ioDispatcher: CoroutineDispatcher,
) {

    var userAccount: Account? = null

    fun getAccount(): Single<Either<ServerError, Account>> {
        return zipRequestsUtil.issueApiCall(GetAccountRequest())
            .flatMap {
                when (it) {
                    is Either.Value -> Single.just(it)
                    is Either.Error -> Single.just(it)
                    else -> Single.just(Either.Empty())
                }
            }
    }

    suspend fun getAccountV2(): Either<ServerError, Account> {
        return zipRequestsUtil.issueApiCallV2(GetAccountRequest())
    }

    fun sendVerificationLink(accountId: String): Either<ServerError, SendVerificationLink> {
        return zipRequestsUtil.issueApiCallV2(PostAccountVerificationLinkRequest(accountId))
    }

    private fun getAccountOnSignIn(includeLastAttendance: Boolean = false): Single<out Either<ServerError, Account>> {
        return zipRequestsUtil.issueApiCall(
            GetAccountRequest(
                includeMembership = true,
                includeProfile = true,
                includeLocalHouse = true,
                includeLastAttendance = includeLastAttendance
            )
        ).flatMap {
            when (it) {
                is Either.Value -> {
                    workManager.enqueueUniquePeriodicWork(
                        VenueWorker.NAME,
                        ExistingPeriodicWorkPolicy.REPLACE,
                        VenueWorker.periodicRequest()
                    )
                    Single.just(value(saveUser(it.value)))
                }
                is Either.Error -> Single.just(it)
                else -> Single.just(Either.Empty())
            }
        }
    }

    fun getCompleteAccount(
        refreshAccount: Boolean = false,
        includeLastAttendance: Boolean = false
    ): Single<Either<ServerError, Account>> {
        if (!refreshAccount) {
            userAccount?.let { return getCachedAccount(it).subscribeOn(Schedulers.io()) }
        }

        return zipRequestsUtil.issueApiCall(
            GetAccountRequest(
                includeMembership = true,
                includeProfile = true,
                includeLocalHouse = true,
                includeLastAttendance = includeLastAttendance
            )
        ).compose(cacheUserAccount())
    }

    fun getCompleteAccountV2(
        refreshAccount: Boolean = false,
        includeLastAttendance: Boolean = false
    ): Either<ServerError, Account> {

        if (!refreshAccount) {
            userAccount?.let { return Either.Value(it) }
        }

        return zipRequestsUtil.issueApiCallV2(
            GetAccountRequest(
                includeMembership = true,
                includeProfile = true,
                includeLocalHouse = true,
                includeLastAttendance = includeLastAttendance
            )
        ).fold(
            { Either.Error(it) },
            { account ->
                val temporaryAccount = saveUser(account)
                Either.Value(temporaryAccount)
            },
            { Either.Empty() }
        )
    }

    private fun saveUser(account: Account): Account {
        userManager.saveUser(account)
        userAccount = account
        return account
    }

    fun signIn(
        email: String,
        password: String,
        clientSecret: String,
        clientID: String
    ): Single<Either<ServerError, Account>> {
        return requestLogin(email, password, clientSecret, clientID)
            .flatMap { response ->
                when (response) {
                    is Either.Value -> {
                        userAccount = null
                        getAccountOnSignIn()
                    }
                    is Either.Error -> {
                        Single.just(response)
                    }
                    else -> {
                        Single.just(Either.Empty())
                    }
                }
            }
    }

    fun changePassword(
        email: String,
        oldPassword: String,
        newPassword: String,
        newPasswordConfirm: String,
        clientSecret: String,
        clientID: String
    ): Single<Either<ServerError, LoginResponse>> {
        return authenticationRequestFactory.create(
            ChangePasswordRequest(
                oldPassword,
                newPassword,
                newPasswordConfirm
            )
        ).flatMap {
            when (it) {
                is Either.Error -> {
                    Single.just(it)
                }
                is Either.Value -> {
                    throw IllegalStateException("Expected empty body.")
                }
                else -> {
                    requestLogin(email, newPassword, clientSecret, clientID)
                }
            }
        }
    }

    fun canAccess(venue: Venue): Boolean {
        return venue.venueType in arrayOf(
            HouseType.STUDIO.name,
            HouseType.RESTAURANT.name
        ) || canAccess(venue.id)
    }

    fun canAccess(id: String?): Boolean {
        if (id == null || id.isEmpty()) return false
        return userAccount?.accessibleVenuesIds?.contains(id) ?: false
    }

    fun getVenueAttendanceId(): String {
        return userAccount?.attendance?.id ?: ""
    }

    fun isFirstVenueVisit(): Boolean {
        return userAccount?.attendance?.firstVisit ?: false
    }

    fun getAttendingVenue(): Venue? {
        return if (isAttending()) getLatestAttendedVenue() else null
    }

    @SuppressLint("DefaultLocale")
    private fun getLatestAttendedVenue(): Venue? {
        val venueID = getLastAttendedVenueId() ?: return null
        return appComponent.venueRepo.venues().findById(venueID.toUpperCase())
    }

    private fun getLastAttendedVenueId() = userAccount?.attendance?.venueResource?.get()?.id

    fun isAttending(): Boolean {
        return (userAccount?.attendance?.isVisiting() == true && getLatestAttendedVenue().isOpenNow())
    }

    fun updateCachedProfile(profile: Profile) {
        userAccount = userAccount?.copy(profileIdentifier = HasOne(profile))
    }

    fun logout() {
        userSessionManager.logout()
    }

    private fun requestLogin(
        email: String,
        password: String,
        clientSecret: String,
        clientID: String
    ): Single<Either<ServerError, LoginResponse>> {
        return authenticationRequestFactory.create(
            LoginRequest(
                email,
                password,
                clientSecret,
                clientID
            )
        ).doOnSuccess { response ->
            response.fold({
                Timber.d(it.toString())
            }, {
                userSessionManager.token = it.accessToken
                userSessionManager.refreshToken = it.refreshToken
                firebaseRegistrationService.registerFcmToken()
            }, {})
        }
    }

    private fun getCachedAccount(userAccount: Account): Single<Either<ServerError, Account>> {
        return Single.just(value(userAccount))
    }

    private fun cacheUserAccount(): SingleTransformer<Either<ServerError, Account>, Either<ServerError, Account>> {
        return SingleTransformer { single ->
            single.flatMap {
                when (it) {
                    is Either.Value -> {
                        return@flatMap Single.just(value(saveUser(it.value)))
                    }
                    is Either.Error -> Single.just(it)
                    else -> Single.just(Either.Empty())
                }
            }
        }
    }
}