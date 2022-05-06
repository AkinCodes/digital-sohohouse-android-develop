package com.sohohouse.seven.interactors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.work.WorkManager
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.times
import com.sohohouse.seven.FeatureFlags
import com.sohohouse.seven.common.house.HouseManager
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.user.UserSessionManager
import com.sohohouse.seven.fcm.FirebaseRegistrationService
import com.sohohouse.seven.network.auth.AuthenticationRequestFactory
import com.sohohouse.seven.network.auth.model.LoginResponse
import com.sohohouse.seven.network.auth.request.LoginRequest
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.CoreRequestFactory
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.models.Account
import com.sohohouse.seven.network.core.models.Membership
import com.sohohouse.seven.network.core.request.GetAccountRequest
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.ObjectDocument
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class AccountInteractorTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var accountInteractor: AccountInteractor

    lateinit var accountInteractorSpy: AccountInteractor

    @Mock
    lateinit var authenticationRequestFactory: AuthenticationRequestFactory

    @Mock
    lateinit var zipRequestsUtil: ZipRequestsUtil

    @Mock
    lateinit var coreRequestFactory: CoreRequestFactory

    @Mock
    lateinit var userSessionManager: UserSessionManager

    @Mock
    lateinit var userManager: UserManager

    @Mock
    lateinit var houseManager: HouseManager

    @Mock
    lateinit var featureFlags: FeatureFlags

    @Mock
    lateinit var firebaseRegistrationService: FirebaseRegistrationService

    @Mock
    lateinit var workManager: WorkManager

    @MockK(relaxed = true)
    lateinit var sohoApiService: SohoApiService

    val authClientSecret = "ea17035c3ed73d551b6322db7c3"

    val authApplicationId = "c5b656450b07b5cb9540b5b9de"

    val testAccountID = "testAccountId"

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MockKAnnotations.init(this)

        accountInteractor = AccountInteractor(
            authenticationRequestFactory,
            zipRequestsUtil,
            userSessionManager,
            userManager,
            firebaseRegistrationService,
            workManager,
            sohoApiService,
            Dispatchers.Unconfined
        )

        accountInteractorSpy = Mockito.spy(accountInteractor)
        Mockito.`when`(featureFlags.checkEmailVerified).thenReturn(true)

    }

    @After
    fun tearDown() {
        Mockito.reset(
            authenticationRequestFactory,
            zipRequestsUtil,
            coreRequestFactory,
            userManager
        )
    }

    @Test
    fun `user enters correct credential, store tokens`() {
        // GIVEN the user enters correct details
        val (email, password, validLogin) = setupValidLoginResponse()

        val account = getTestAccount()

        val loginResponse = LoginResponse()
        loginResponse.accessToken = validLogin.accessToken
        loginResponse.refreshToken = validLogin.refreshToken
        val response: Single<Either<ServerError, LoginResponse>> = Single.just(value(loginResponse))

        Mockito.`when`(authenticationRequestFactory.create(any<LoginRequest>()))
            .thenReturn(response)

        Mockito.`when`(zipRequestsUtil.issueApiCall(any<GetAccountRequest>()))
            .thenReturn(Single.just(value(account)))

        // WHEN the user logs in successfully
        accountInteractor.signIn(email, password, "fdsfsdf", "fdsfsdf").subscribe()

        // THEN auth token and refresh token is stored in secure prefs
        Mockito.verify(userSessionManager).token = validLogin.accessToken
        Mockito.verify(userSessionManager).refreshToken = validLogin.refreshToken
    }

    @Test
    fun `user enters right credential, user is saved in userManager`() {
        // GIVEN the user enters correct details, and is verified
        val (email, password, validLogin) = setupValidLoginResponse()

        val account = getTestAccount(isEmailVerified = true)

        val loginResponse = LoginResponse()
        loginResponse.accessToken = validLogin.accessToken
        loginResponse.refreshToken = validLogin.refreshToken
        val response: Single<Either<ServerError, LoginResponse>> = Single.just(value(loginResponse))

        Mockito.`when`(authenticationRequestFactory.create(any<LoginRequest>()))
            .thenReturn(response)

        Mockito.`when`(zipRequestsUtil.issueApiCall(any<GetAccountRequest>()))
            .thenReturn(Single.just(value(account)))

        // WHEN the user logs in successfully
        accountInteractor.signIn(email, password, authClientSecret, authApplicationId).subscribe()

        // THEN user is saved in userManager
        Mockito.verify(userManager, times(1)).saveUser(account)
    }

    private fun getTestAccount(isEmailVerified: Boolean = true): Account {
        val membership = Membership(
            _status = "CURRENT",
            _membershipType = "REGULAR",
            _subscriptionType = "EVERY"
        )
        val account = Account(
            membershipResource = HasOne(membership),
            emailVerified = isEmailVerified
        ).apply {
            id = testAccountID
        }
        val document = ObjectDocument(membership)
        document.addInclude(membership)

        account.document = document
        return account
    }

    private fun setupValidLoginResponse(): Triple<String, String, LoginResponse> {
        val email = "test"
        val password = "test"
        val token = "123"
        val refreshToken = "456"
        val loginResponse = LoginResponse()
        loginResponse.accessToken = token
        loginResponse.refreshToken = refreshToken
        return Triple(email, password, loginResponse)
    }
}