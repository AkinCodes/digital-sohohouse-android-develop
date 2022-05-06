package com.sohohouse.seven.apponboarding

import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import com.sohohouse.seven.app.TestApp
import com.sohohouse.seven.apponboarding.houseboard.OnboardingHouseBoardFragment
import com.sohohouse.seven.apponboarding.houseboardpost.OnboardingHouseBoardPostFragment
import com.sohohouse.seven.apponboarding.housepreferences.OnboardingHousePreferencesFragment
import com.sohohouse.seven.apponboarding.welcome.AppOnboardingWelcomeFragment
import com.sohohouse.seven.base.BaseFragment
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.intro.IntroActivity
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, application = TestApp::class)
class AppOnboardingFlowManagerTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var flowManager: AppOnboardingFlowManager

    @Mock
    private lateinit var activity: AppOnboardingActivity

    @Mock
    private lateinit var supportFragmentManager: FragmentManager

    @Mock
    private lateinit var fragmentTransaction: FragmentTransaction

    @MockK
    private lateinit var userManager: UserManager

    @Mock
    lateinit var zipRequestsUtil: ZipRequestsUtil

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MockKAnnotations.init(this)
        flowManager = AppOnboardingFlowManager(userManager)
    }

    @Test
    fun `if user starts onboarding, presented with welcome fragment`() {
        every { userManager.subscriptionType } returns SubscriptionType.EVERY

        Mockito.`when`(activity.supportFragmentManager).thenReturn(supportFragmentManager)
        Mockito.`when`(supportFragmentManager.beginTransaction()).thenReturn(fragmentTransaction)
        Mockito.`when`(fragmentTransaction.replace(any(), any<AppOnboardingWelcomeFragment>()))
            .thenReturn(fragmentTransaction)

        // GIVEN that the user has not been app onboard
        // WHEN transitioning to next screen
        flowManager.navigateToNext(activity, AppOnboardScreen.INTRO)

        // THEN the next screen is welcome fragment
        verify(fragmentTransaction).commit()
    }

    @Test
    fun `if friends user selects next on welcome, presented with finish fragment`() {
        every { userManager.subscriptionType } returns SubscriptionType.FRIENDS

        Mockito.`when`(activity.supportFragmentManager).thenReturn(supportFragmentManager)
        Mockito.`when`(supportFragmentManager.beginTransaction()).thenReturn(fragmentTransaction)
        Mockito.`when`(fragmentTransaction.replace(any(), any<BaseFragment>()))
            .thenReturn(fragmentTransaction)
        Mockito.`when`(fragmentTransaction.addToBackStack(any())).thenReturn(fragmentTransaction)

        // GIVEN that the user is on welcome screen
        // WHEN transitioning to next screen
        flowManager.navigateToNext(activity, AppOnboardScreen.WELCOME)

        // THEN the next screen is intro activity
        val argument = ArgumentCaptor.forClass(Intent::class.java)
        Mockito.verify(activity).startActivity(argument.capture())
        Assertions.assertThat(argument.value.component?.className)
            .endsWith(IntroActivity::class.java.simpleName)
    }

    @Test
    fun `if house member user selects next on welcome, presented with tailor fragment`() {
        every { userManager.subscriptionType } returns SubscriptionType.EVERY

        Mockito.`when`(activity.supportFragmentManager).thenReturn(supportFragmentManager)
        Mockito.`when`(supportFragmentManager.beginTransaction()).thenReturn(fragmentTransaction)
        Mockito.`when`(
            fragmentTransaction.replace(
                any(),
                any<OnboardingHousePreferencesFragment>()
            )
        ).thenReturn(fragmentTransaction)
        Mockito.`when`(fragmentTransaction.addToBackStack(any())).thenReturn(fragmentTransaction)

        // GIVEN that the user is on welcome screen
        // WHEN transitioning to next screen
        flowManager.navigateToNext(activity, AppOnboardScreen.WELCOME)

        // THEN the next screen is tailor fragment
        verify(fragmentTransaction).commit()
    }

    @Test
    fun `if user selects next on welcome, the previous fragment is added to the stack`() {
        every { userManager.subscriptionType } returns SubscriptionType.EVERY

        Mockito.`when`(activity.supportFragmentManager).thenReturn(supportFragmentManager)
        Mockito.`when`(supportFragmentManager.beginTransaction()).thenReturn(fragmentTransaction)
        Mockito.`when`(
            fragmentTransaction.replace(
                any(),
                any<OnboardingHousePreferencesFragment>()
            )
        ).thenReturn(fragmentTransaction)
        Mockito.`when`(fragmentTransaction.addToBackStack(any())).thenReturn(fragmentTransaction)

        flowManager.navigateToNext(activity, AppOnboardScreen.WELCOME)

        verify(fragmentTransaction).addToBackStack(AppOnboardScreen.WELCOME.name)
    }


    @Test
    fun `if user selects next on tailor, presented with house board fragment`() {
        every { userManager.subscriptionType } returns SubscriptionType.EVERY

        Mockito.`when`(activity.supportFragmentManager).thenReturn(supportFragmentManager)
        Mockito.`when`(supportFragmentManager.beginTransaction()).thenReturn(fragmentTransaction)
        Mockito.`when`(fragmentTransaction.replace(any(), any<OnboardingHouseBoardFragment>()))
            .thenReturn(fragmentTransaction)
        Mockito.`when`(fragmentTransaction.addToBackStack(any())).thenReturn(fragmentTransaction)

        // GIVEN that the user is on tailor screen
        // WHEN transitioning to next screen
        flowManager.navigateToNext(activity, AppOnboardScreen.TAILOR)

        // THEN the next screen is house notes fragment
        verify(fragmentTransaction).commit()
    }

    @Test
    fun `if user selects next on tailor, the previous fragment is added to the stack`() {
        every { userManager.subscriptionType } returns SubscriptionType.EVERY

        Mockito.`when`(activity.supportFragmentManager).thenReturn(supportFragmentManager)
        Mockito.`when`(supportFragmentManager.beginTransaction()).thenReturn(fragmentTransaction)
        Mockito.`when`(fragmentTransaction.replace(any(), any<OnboardingHouseBoardFragment>()))
            .thenReturn(fragmentTransaction)
        Mockito.`when`(fragmentTransaction.addToBackStack(any())).thenReturn(fragmentTransaction)

        flowManager.navigateToNext(activity, AppOnboardScreen.TAILOR)

        verify(fragmentTransaction).addToBackStack(AppOnboardScreen.TAILOR.name)
    }

    @Test
    fun `if user selects next on house board, presented with house board post fragment`() {
        every { userManager.subscriptionType } returns SubscriptionType.EVERY

        Mockito.`when`(activity.supportFragmentManager).thenReturn(supportFragmentManager)
        Mockito.`when`(supportFragmentManager.beginTransaction()).thenReturn(fragmentTransaction)
        Mockito.`when`(fragmentTransaction.replace(any(), any<OnboardingHouseBoardPostFragment>()))
            .thenReturn(fragmentTransaction)
        Mockito.`when`(fragmentTransaction.addToBackStack(any())).thenReturn(fragmentTransaction)

        // GIVEN that the user is on house board screen
        // WHEN transitioning to next screen
        flowManager.navigateToNext(activity, AppOnboardScreen.HOUSE_BOARD)

        // THEN the next screen is house board post fragment
        verify(fragmentTransaction).commit()
    }

    @Test
    fun `if user selects next on house board, the previous fragment is added to the stack`() {
        every { userManager.subscriptionType } returns SubscriptionType.EVERY

        Mockito.`when`(activity.supportFragmentManager).thenReturn(supportFragmentManager)
        Mockito.`when`(supportFragmentManager.beginTransaction()).thenReturn(fragmentTransaction)
        Mockito.`when`(fragmentTransaction.replace(any(), any<OnboardingHouseBoardPostFragment>()))
            .thenReturn(fragmentTransaction)
        Mockito.`when`(fragmentTransaction.addToBackStack(any())).thenReturn(fragmentTransaction)

        flowManager.navigateToNext(activity, AppOnboardScreen.HOUSE_BOARD)

        verify(fragmentTransaction).addToBackStack(AppOnboardScreen.HOUSE_BOARD.name)
    }

    @Test
    fun `if user selects next on house board post, presented with finish fragment`() {
        every { userManager.subscriptionType } returns SubscriptionType.EVERY

        Mockito.`when`(activity.supportFragmentManager).thenReturn(supportFragmentManager)
        Mockito.`when`(supportFragmentManager.beginTransaction()).thenReturn(fragmentTransaction)
        Mockito.`when`(fragmentTransaction.replace(any(), any<BaseFragment>()))
            .thenReturn(fragmentTransaction)
        Mockito.`when`(fragmentTransaction.addToBackStack(any())).thenReturn(fragmentTransaction)

        // GIVEN that the user is on house board post screen
        // WHEN transitioning to next screen
        flowManager.navigateToNext(activity, AppOnboardScreen.HOUSE_BOARD_POST)

        // THEN the next screen is intro activity
        val argument = ArgumentCaptor.forClass(Intent::class.java)
        Mockito.verify(activity).startActivity(argument.capture())
        Assertions.assertThat(argument.value.component?.className)
            .endsWith(IntroActivity::class.java.simpleName)
    }

}