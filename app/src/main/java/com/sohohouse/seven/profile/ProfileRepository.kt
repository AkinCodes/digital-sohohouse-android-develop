package com.sohohouse.seven.profile

import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.empty
import com.sohohouse.seven.network.base.model.error
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.CoreRequestFactory
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.isSuccessful
import com.sohohouse.seven.network.core.models.Account
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.network.core.models.ProfileAccountUpdate
import com.sohohouse.seven.network.core.models.ShortProfileUrlResponse
import com.sohohouse.seven.network.core.request.*
import io.reactivex.Single
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val coreRequestFactory: CoreRequestFactory,
    private val userManager: UserManager,
    private val sohoApiService: SohoApiService
) {

    private val _shortProfileUrl = MutableStateFlow(ShortProfileUrlResponse())
    val shortProfileUrl = _shortProfileUrl.asStateFlow()

    fun getMyAccountWithProfile(): Single<Either<ServerError, Account>> {
        return coreRequestFactory.create(
            GetAccountRequest(
                includeProfile = true,
                includeInterests = true,
                includeLocalHouse = true
            )
        )
            .doOnSuccess {
                if (it is Either.Value) {
                    onAccountWithProfileFetched(it.value)
                }
            }
    }

    fun getMyAccountWithProfileV2(): Either<ServerError, Account> {
        return coreRequestFactory.createV2(
            GetAccountRequest(
                includeProfile = true,
                includeInterests = true,
                includeLocalHouse = true
            )
        )
            .ifValue {
                onAccountWithProfileFetched(it)
            }
    }

    private fun onAccountWithProfileFetched(account: Account) {
        userManager.saveUser(account)
    }

    private fun onProfileFetched(profile: Profile) {
        userManager.saveProfile(profile)
    }

    fun getMyProfile(): Either<ServerError, Profile> {
        return coreRequestFactory.createV2(GetMyProfileRequest())
    }

    fun getProfile(id: String): Either<ServerError, Profile> {
        return coreRequestFactory.createV2(GetProfileRequest(id))
    }

    fun saveProfileWithAccountUpdate(profile: Profile, accountUpdate: ProfileAccountUpdate?)
            : Single<Either<ServerError, Any>> {
        //save profile, then account, so account returns with up-to-date profile so we can cache it
        return coreRequestFactory.create(PatchProfileRequest(profile))
            .flatMap { either ->
                either.fold(
                    ifValue = {
                        if (accountUpdate == null) {
                            return@flatMap Single.just(either)
                        }
                        coreRequestFactory.create(PatchProfileAccountRequest(accountUpdate))
                            .doOnSuccess {
                                if (it is Either.Value) onAccountWithProfileFetched(it.value)
                            }
                    },
                    ifError = { Single.just(Either.Error(it)) },
                    ifEmpty = { Single.just(Either.Empty()) }
                )
            }
    }

    fun saveProfileWithAccountUpdateV2(profile: Profile, accountUpdate: ProfileAccountUpdate?)
            : Either<ServerError, Any> {

        //save profile, then account, so account returns with up-to-date profile so we can cache it
        return coreRequestFactory.createV2(PatchProfileRequest(profile))
            .fold(ifValue = {
                if (accountUpdate == null) {
                    onProfileFetched(it)
                    return value(it)
                }
                coreRequestFactory.createV2(PatchProfileAccountRequest(accountUpdate))
                    .fold<Either<ServerError, Account>>(ifValue = {
                        onAccountWithProfileFetched(it)
                        return value(it)
                    }, ifEmpty = {
                        return empty()
                    }, ifError = {
                        return error(it)
                    })
            }, ifEmpty = {
                return empty()
            }, ifError = {
                return error(it)
            })
    }

    suspend fun loadShortProfileUrl(profileId: String) {
        val result = sohoApiService.createShortProfilesUrls(profileId)
        if (result.isSuccessful()) {
            _shortProfileUrl.value = result.response
        }
    }

}