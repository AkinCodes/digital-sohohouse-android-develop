package com.sohohouse.seven.memberonboarding

import android.content.Context
import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sohohouse.seven.app.TestApp
import com.sohohouse.seven.authentication.AuthenticationFlowManager
import com.sohohouse.seven.base.BaseActivity
import com.sohohouse.seven.book.eventdetails.bookingsuccess.BookingSuccessActivity
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.main.MainActivity
import com.sohohouse.seven.memberonboarding.induction.confirmation.InductionConfirmationActivity
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.Venue
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, application = TestApp::class)
class MemberOnboardingFlowManagerTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @InjectMocks
    private lateinit var flowManager: MemberOnboardingFlowManager

    @Mock
    private lateinit var activity: BaseActivity

    @Mock
    private lateinit var onboardingFlowManager: AuthenticationFlowManager

    @Mock
    lateinit var userManager: UserManager

    @Mock
    lateinit var venue: Venue

    @Mock
    lateinit var event: Event

    @Mock
    lateinit var context: Context


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `appointment confirmation shows booking activity `() {
        // WHEN transitioning to next step
        val intent =
            flowManager.navigateToAppointmentSuccess(activity, "", "", null, "", "", "", "")

        // THEN the next step will be the login screen
        intent.component?.let {
            Assertions.assertThat(it.className)
                .endsWith(BookingSuccessActivity::class.java.simpleName)
        }
    }

    @Test
    fun `followup confirmation shows booking activity `() {
        // WHEN transitioning to next step
        val intent = flowManager.navigateToFollowUpSuccess(activity, "", "", "", "", "")

        // THEN the next step will be the login screen
        intent.component?.let {
            Assertions.assertThat(it.className)
                .endsWith(BookingSuccessActivity::class.java.simpleName)
        }
    }

    @Test
    fun `after appointment success, shows confirmation `() {
        // WHEN transitioning to next step
        val intent = flowManager.navigateAfterAppointmentSuccess(activity, event, venue, "")

        // THEN the next step will be the login screen
        intent.component?.let {
            Assertions.assertThat(it.className)
                .endsWith(InductionConfirmationActivity::class.java.simpleName)
        }
    }

    @Test
    fun `after followup success, shows confirmation `() {
        // WHEN transitioning to next step
        val intent = flowManager.navigateAfterFollowUpSuccess(activity, venue)

        // THEN the next step will be the login screen
        intent.component?.let {
            Assertions.assertThat(it.className)
                .endsWith(InductionConfirmationActivity::class.java.simpleName)
        }
    }

    @Test
    fun `after confirmation, if not inducted, go to home screen`() {
        // WHEN transitioning to next step
        mockUserManager(true)
        flowManager.navigateAfterConfirmation(activity)

        // THEN the next screen is account status screen
        val argument = ArgumentCaptor.forClass(Intent::class.java)
        Mockito.verify(activity).startActivity(argument.capture())
        argument.value.component?.let {
            Assertions.assertThat(it.className)
                .endsWith(MainActivity::class.java.simpleName)
        }
    }

    @Test
    fun `after confirmation, if changed appointment from planner, finish such that return to planner`() {
        // WHEN transitioning to next step
        mockUserManager(true)
        flowManager.navigateAfterConfirmation(activity)

        // THEN the next screen is account status screen
//        val argument = ArgumentCaptor.forClass(Intent::class.java)
        Mockito.verify(activity).finish()
    }

    fun mockUserManager(isInducted: Boolean) {
        Mockito.`when`(userManager.isInducted).thenReturn(isInducted)
    }

}