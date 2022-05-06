package com.sohohouse.seven.authentication.signin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.sohohouse.seven.app.TestApp
import com.sohohouse.seven.base.filter.Filter
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.remoteconfig.RemoteConfigManager
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.TestCoroutineRule
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.models.Account
import com.sohohouse.seven.network.core.models.AccountOnboarding
import com.sohohouse.seven.network.core.models.Membership
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import moe.banana.jsonapi2.HasOne
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, application = TestApp::class)
class SignInViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    lateinit var signInPresenter: SignInViewModel

    @Mock
    lateinit var authInteractor: ZipRequestsUtil

    @Mock
    lateinit var accountInteractor: AccountInteractor

    @Mock
    lateinit var account: Account

    @Mock
    lateinit var membership: Membership

    @Mock
    lateinit var membershipRelationship: HasOne<Membership>

    @Mock
    lateinit var analyticsManager: AnalyticsManager

    @Mock
    lateinit var userManager: UserManager

    @MockK
    lateinit var sohoApiService: SohoApiService

    @MockK
    lateinit var remoteConfigManager: RemoteConfigManager

    val authClientSecret = "ea17035c3ed73d551b6322db7c3"

    val authApplicationId = "c5b656450b07b5cb9540b5b9de"

    var filter: Filter? = null

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MockKAnnotations.init(this)
        signInPresenter = SignInViewModel(
            analyticsManager,
            accountInteractor,
            userManager,
            Dispatchers.Unconfined,
            sohoApiService,
            remoteConfigManager
        )
    }

    @After
    fun tearDown() {
        Mockito.reset(
            authInteractor,
            account,
            membership,
            membershipRelationship
        )
        filter = null
    }

    @Test
    fun `user tries to log in, spinner appears`() {
        // GIVEN the user enters some credentials
        val (email, password) = setupValidLoginResponse()
        val observer = mockk<Observer<Any>>(relaxed = true)
        signInPresenter.loadingState.observeForever(observer)

        // WHEN the user tries to log in
        Mockito.`when`(
            accountInteractor.signIn(
                email,
                password,
                authClientSecret,
                authApplicationId
            )
        )
            .thenReturn(Single.just(value(account)))

        signInPresenter.login(email, password, authClientSecret, authApplicationId)
        // THEN the loading spinner appears

        io.mockk.verify(exactly = 1) { observer.onChanged(LoadingState.Loading) }
        io.mockk.verify(exactly = 1) { observer.onChanged(LoadingState.Idle) }

    }

    @Test
    fun `user enters correct credential, navigates to next screen in flow`() = runBlockingTest {
        // GIVEN the user enters correct credentials
        val (email, password) = setupValidLoginResponse()
        val observer = mockk<Observer<Any>>(relaxed = true)
        signInPresenter.loginSuccessEvent.observeForever(observer)

        // WHEN the user tries to log in
        Mockito.`when`(
            accountInteractor.signIn(
                email,
                password,
                authClientSecret,
                authApplicationId
            )
        )
            .thenReturn(Single.just(value(account)))

        val accountOnboarding = mockk<AccountOnboarding>(relaxed = true)
        coEvery { sohoApiService.getAccountOnboardingStatus() } returns ApiResponse.Success(
            accountOnboarding
        )

        signInPresenter.login(email, password, authClientSecret, authApplicationId)

        // THEN login succeeds
        verify { observer.onChanged(any()) }
    }

    @Test
    fun `user enters wrong credential, dialog appears`() {
        // GIVEN the user enters wrong credentials
        val (email, password) = setupValidLoginResponse()
        val errorObserver = mockk<Observer<Array<out String>>>(relaxed = true)
        signInPresenter.showGenericErrorDialogEvent.observeForever(errorObserver)
        val loadingObserver = mockk<Observer<Any>>(relaxed = true)
        signInPresenter.loadingState.observeForever(loadingObserver)

        // WHEN the user tries to log in
        Mockito.`when`(
            accountInteractor.signIn(
                email,
                password,
                authClientSecret,
                authApplicationId
            )
        )
            .thenReturn(Single.just(com.sohohouse.seven.network.base.model.error(ServerError.COMPLETE_MELTDOWN)))

        signInPresenter.login(email, password, authClientSecret, authApplicationId)

        // THEN login fails
        io.mockk.verify(exactly = 1) { loadingObserver.onChanged(LoadingState.Idle) }
        io.mockk.verify { errorObserver.onChanged(any()) }
    }

    private fun setupValidLoginResponse(): Pair<String, String> {
        val email = "test"
        val password = "test"
        return Pair(email, password)
    }

}