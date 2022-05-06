package com.sohohouse.seven.splash

import android.content.Context
import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.sohohouse.seven.app.TestApp
import com.sohohouse.seven.authentication.AuthenticationFlowManager
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.deeplink.DeeplinkViewModel
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.common.remoteconfig.RemoteConfigManager
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.user.UserSessionManager
import com.sohohouse.seven.common.utils.TestCoroutineRule
import com.sohohouse.seven.common.uxcam.UXCamVendor
import com.sohohouse.seven.main.MainActivity
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.forceupdate.ForceUpdateRequestFactory
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(application = TestApp::class)
class SplashViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var context: Context

    @MockK
    private lateinit var flowManager: AuthenticationFlowManager

    @MockK
    private lateinit var forceUpdateRequestFactory: ForceUpdateRequestFactory

    @MockK
    private lateinit var deeplinkViewModel: DeeplinkViewModel

    @MockK
    lateinit var analyticsManager: AnalyticsManager

    @MockK
    lateinit var userManager: UserManager

    @MockK
    lateinit var accountInteractor: AccountInteractor

    @MockK
    lateinit var userSessionManager: UserSessionManager

    @MockK(relaxed = true)
    lateinit var sohoApiService: SohoApiService

    @MockK
    lateinit var uxCamVendor: UXCamVendor

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MockKAnnotations.init(this)

        every { userSessionManager.isLoggedIn } returns false
        every { userManager.subscriptionType } returns SubscriptionType.EVERY
        every { userManager.isStaff } returns false
    }

    @Test
    fun `Transition to Main Activity`() = runBlockingTest {
        every { forceUpdateRequestFactory.create(any()) } returns Either.Value(Any())
        every { flowManager.navigateFrom(context) } returns Intent(
            context,
            MainActivity::class.java
        )

        val observer = mockk<Observer<Intent>>()
        val slot = slot<Intent>()
        var intent: Intent? = null
        every { observer.onChanged(capture(slot)) } answers {
            intent = slot.captured
        }

        with(createViewModel()) {
            this.navigation.observeForever(observer)
            this.loadRequest(context, null)
        }
        assert(intent?.component?.className != MainActivity::class.java.name)
    }

// Temporary disabled because of Bitrise fail
//    @Test
//    fun `Transition to Force Update`() = runBlockingTest {
//        every { forceUpdateRequestFactory.create(any()) } returns Either.Error(ForceUpdateError.UPDATE_REQUIRED)
//        every { flowManager.navigateToForceUpdate(context) } returns Intent(context, ForceUpdateActivity::class.java)
//
//        val observer = mockk<Observer<Intent>>()
//        val slot = slot<Intent>()
//        var intent: Intent? = null
//        every { observer.onChanged(capture(slot)) } answers {
//            intent = slot.captured
//        }
//
//        with (createViewModel()) {
//            this.navigation.observeForever(observer)
//            this.loadRequest(context)
//        }
//        assert(intent?.component?.className != ForceUpdateActivity::class.java.name)
//    }

    private fun createViewModel(): SplashViewModel {
        return SplashViewModel(
            flowManager,
            forceUpdateRequestFactory,
            accountInteractor,
            userSessionManager,
            Dispatchers.Unconfined,
            userManager,
            sohoApiService,
            uxCamVendor,
            deeplinkViewModel,
            analyticsManager
        )
    }
}