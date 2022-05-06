package com.sohohouse.seven.authentication

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sohohouse.seven.FeatureFlags
import com.sohohouse.seven.accountstatus.AccountStatusActivity
import com.sohohouse.seven.app.TestApp
import com.sohohouse.seven.apponboarding.AppOnboardingActivity
import com.sohohouse.seven.apponboarding.data.OnboardingDataActivity
import com.sohohouse.seven.apponboarding.optinrecommendations.LandingOptInRecommendationsActivity
import com.sohohouse.seven.apponboarding.terms.OnboardingTermsActivity
import com.sohohouse.seven.common.user.*
import com.sohohouse.seven.intro.IntroActivity
import com.sohohouse.seven.main.MainActivity
import com.sohohouse.seven.welcome.WelcomeActivity
import org.assertj.core.api.Assertions
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, application = TestApp::class)
class AuthenticationFlowManagerTest() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @InjectMocks
    private lateinit var authFlowManager: AuthenticationFlowManager

    @Mock
    lateinit var context: Context

    @Mock
    lateinit var userManager: UserManager

    @Mock
    lateinit var userSessionManager: UserSessionManager

    @Mock
    lateinit var featureFlags: FeatureFlags

    val testAccountId: String = "testAccountID"

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        `when`(featureFlags.checkEmailVerified).thenReturn(true)
    }

    @Test
    fun `if user has not logged in, show welcome activity`() {
        // GIVEN that the user has not done member onboarding,
        // WHEN transitioning to next screen
        mockUserManager(
            isLoggedIn = false,
            didConsentTerms = false,
            analyticsConsent = AnalyticsConsent.NONE,
            membershipStatus = MembershipStatus.APPROVED,
            canAccessApp = true,
            isInducted = false,
            hasSeenWelcomeScreen = false,
            hasSeenBenefitsScreen = false,
            isAppOnboardingComplete = false,
            isEmailVerified = false
        )

        // THEN the next screen is member onboarding
        val intent = authFlowManager.navigateFrom(context)
        Assertions.assertThat(intent.component?.className)
            .endsWith(WelcomeActivity::class.java.simpleName)
    }

    @Test
    fun `if user has not completed onboarding welcome screen, show verify Onboarding Welcome activity`() {
        // GIVEN that the user has verified their email
        // WHEN transitioning to next screen
        mockUserManager(
            isLoggedIn = true,
            didConsentTerms = true,
            analyticsConsent = AnalyticsConsent.ACCEPTED,
            membershipStatus = MembershipStatus.APPROVED,
            canAccessApp = true,
            isInducted = true,
            hasSeenWelcomeScreen = false,
            hasSeenBenefitsScreen = false,
            isAppOnboardingComplete = true
        )

        // THEN the next screen verify account
        val intent = authFlowManager.navigateFrom(context)
        Assertions.assertThat(intent.component?.className)
            .endsWith(IntroActivity::class.java.simpleName)
    }

    @Test
    fun `if user has not verified email, show verify Email activity`() {
        // GIVEN that the user has verified their email
        // WHEN transitioning to next screen
        mockUserManager(
            isLoggedIn = true,
            didConsentTerms = false,
            analyticsConsent = AnalyticsConsent.NONE,
            membershipStatus = MembershipStatus.APPROVED,
            canAccessApp = true,
            isInducted = false,
            hasSeenWelcomeScreen = false,
            hasSeenBenefitsScreen = false,
            isAppOnboardingComplete = false,
            isEmailVerified = false
        )

        // THEN the next screen verify account
        val intent = authFlowManager.navigateFrom(context)
        Assertions.assertThat(intent.component?.className)
            .endsWith(VerifyAccountActivity::class.java.simpleName)
        Assert.assertEquals(
            testAccountId,
            intent.getStringExtra(VerifyAccountActivity.EXTRA_ACCOUNT_ID)
        )
    }

    @Test
    fun `if user has not agreed to terms, show TnC activity`() {
        // GIVEN that the user has not done member onboarding,
        // WHEN transitioning to next screen
        mockUserManager(
            isLoggedIn = true,
            didConsentTerms = false,
            analyticsConsent = AnalyticsConsent.NONE,
            membershipStatus = MembershipStatus.APPROVED,
            canAccessApp = true,
            isInducted = false,
            hasSeenWelcomeScreen = false,
            hasSeenBenefitsScreen = false,
            isAppOnboardingComplete = false
        )

        // THEN the next screen is member onboarding
        val intent = authFlowManager.navigateFrom(context)
        Assertions.assertThat(intent.component?.className)
            .endsWith(OnboardingTermsActivity::class.java.simpleName)
    }

    @Test
    fun `if user has not decided GDPR, show data terms activity`() {
        // GIVEN that the user has not done member onboarding,
        // WHEN transitioning to next screen
        mockUserManager(
            isLoggedIn = true,
            didConsentTerms = true,
            analyticsConsent = AnalyticsConsent.NONE,
            membershipStatus = MembershipStatus.APPROVED,
            canAccessApp = true,
            isInducted = false,
            hasSeenWelcomeScreen = false,
            hasSeenBenefitsScreen = false,
            isAppOnboardingComplete = false
        )

        // THEN the next screen is member onboarding
        val intent = authFlowManager.navigateFrom(context)
        Assertions.assertThat(intent.component?.className)
            .endsWith(OnboardingDataActivity::class.java.simpleName)
    }

    //    @Test
    fun `if user has not done member onboarding, show member onboarding activity`() {
        // GIVEN that the user has not done member onboarding,
        // WHEN transitioning to next screen
        mockUserManager(
            isLoggedIn = true,
            didConsentTerms = true,
            analyticsConsent = AnalyticsConsent.ACCEPTED,
            membershipStatus = MembershipStatus.APPROVED,
            canAccessApp = true,
            isInducted = false,
            hasSeenWelcomeScreen = true,
            hasSeenBenefitsScreen = false,
            isAppOnboardingComplete = false
        )

        // THEN the next screen is member onboarding
        val intent = authFlowManager.navigateFrom(context)
        Assertions.assertThat(intent.component?.className)
            .endsWith(AppOnboardingActivity::class.java.simpleName)
    }

    @Test
    fun `if user cannot access app, show account status activity`() {
        // GIVEN that the user has not done member onboarding,
        // WHEN transitioning to next screen
        mockUserManager(
            isLoggedIn = true,
            didConsentTerms = true,
            analyticsConsent = AnalyticsConsent.ACCEPTED,
            membershipStatus = MembershipStatus.FROZEN,
            canAccessApp = false,
            isInducted = false,
            hasSeenWelcomeScreen = false,
            hasSeenBenefitsScreen = false,
            isAppOnboardingComplete = false
        )

        // THEN the next screen is member onboarding
        val intent = authFlowManager.navigateFrom(context)
        Assertions.assertThat(intent.component?.className)
            .endsWith(AccountStatusActivity::class.java.simpleName)
    }

    @Test
    fun `if user has done member onboarding, status chasing, has not done chasing flow, show account status`() {
        // GIVEN that the user has done member onboarding, status chasing, has not done chasing flow
        // WHEN transitioning to next screen
        mockUserManager(
            isLoggedIn = true,
            didConsentTerms = true,
            analyticsConsent = AnalyticsConsent.ACCEPTED,
            membershipStatus = MembershipStatus.CHASING,
            canAccessApp = true,
            isInducted = true,
            hasSeenWelcomeScreen = false,
            hasSeenBenefitsScreen = false,
            isAppOnboardingComplete = true
        )

        // THEN the next screen is account status
        val intent = authFlowManager.navigateFrom(context)
        Assertions.assertThat(intent.component?.className)
            .endsWith(AccountStatusActivity::class.java.simpleName)
    }

    @Test
    fun `if user has done member onboarding, status chasing, has done chasing flow, has seen welcome, has done app onboarding, show main activity`() {
        // GIVEN that the the user has done member onboarding, status chasing, has done chasing flow, has done app onboarding
        // WHEN transitioning to next screen
        mockUserManager(
            isLoggedIn = true,
            didConsentTerms = true,
            analyticsConsent = AnalyticsConsent.ACCEPTED,
            membershipStatus = MembershipStatus.APPROVED,
            canAccessApp = true,
            isInducted = true,
            hasSeenWelcomeScreen = true,
            hasSeenBenefitsScreen = true,
            isAppOnboardingComplete = true
        )

        // THEN the next screen is main activity
        val intent = authFlowManager.navigateFrom(context)
        Assertions.assertThat(intent.component?.className)
            .endsWith(MainActivity::class.java.simpleName)
    }

    //    @Test
    fun `if user has done member onboarding, status not chasing, has seen welcome, not done app onboarding, show app onboarding activity`() {
        // GIVEN that the user has done member onboarding, status not chasing, not done app onboarding
        // WHEN transitioning to next screen
        mockUserManager(
            isLoggedIn = true,
            didConsentTerms = true,
            analyticsConsent = AnalyticsConsent.ACCEPTED,
            membershipStatus = MembershipStatus.APPROVED,
            canAccessApp = true,
            isInducted = true,
            hasSeenWelcomeScreen = true,
            hasSeenBenefitsScreen = false,
            isAppOnboardingComplete = false
        )

        // THEN the next screen is app onboarding fragment
        val intent = authFlowManager.navigateFrom(context)
        Assertions.assertThat(intent.component?.className)
            .endsWith(AppOnboardingActivity::class.java.simpleName)
    }

    @Test
    fun `if user has done member onboarding, status chasing, has not done chasing flow, show account status activity`() {
        // GIVEN that the the user has done member onboarding, status chasing, has not done chasing flow
        // WHEN transitioning to next screen
        mockUserManager(
            isLoggedIn = true,
            didConsentTerms = true,
            analyticsConsent = AnalyticsConsent.ACCEPTED,
            membershipStatus = MembershipStatus.CHASING,
            canAccessApp = true,
            isInducted = true,
            hasSeenWelcomeScreen = false,
            hasSeenBenefitsScreen = false,
            isAppOnboardingComplete = true
        )

        // THEN the next screen is account status
        val intent = authFlowManager.navigateFrom(context)
        Assertions.assertThat(intent.component?.className)
            .endsWith(AccountStatusActivity::class.java.simpleName)
    }

    @Test
    fun `if user has done member onboarding, status not chasing, has seen welcome, has done app onboarding, show main activity`() {
        // GIVEN that the the user has done member onboarding, status not chasing, has done app onboarding
        // WHEN transitioning to next screen
        mockUserManager(
            isLoggedIn = true,
            didConsentTerms = true,
            analyticsConsent = AnalyticsConsent.ACCEPTED,
            membershipStatus = MembershipStatus.CURRENT,
            canAccessApp = true,
            isInducted = true,
            hasSeenWelcomeScreen = true,
            hasSeenBenefitsScreen = true,
            isAppOnboardingComplete = true
        )

        // THEN the next screen is main activity
        val intent = authFlowManager.navigateFrom(context)
        Assertions.assertThat(intent.component?.className)
            .endsWith(MainActivity::class.java.simpleName)
    }

    @Test
    fun `if user has done member onboarding, status not chasing, has not seen welcome, show onboarding welcome activity`() {
        mockUserManager(
            isLoggedIn = true,
            didConsentTerms = true,
            analyticsConsent = AnalyticsConsent.ACCEPTED,
            membershipStatus = MembershipStatus.CURRENT,
            canAccessApp = true,
            isInducted = true,
            hasSeenWelcomeScreen = false,
            hasSeenBenefitsScreen = false,
            isAppOnboardingComplete = true
        )

        // THEN the next screen is main activity
        val intent = authFlowManager.navigateFrom(context)
        Assertions.assertThat(intent.component?.className)
            .endsWith(IntroActivity::class.java.simpleName)
    }

    @Test
    fun `if user has done member onboarding, has not seen opt in for recommendations show onboarding welcome activity`() {
        mockUserManager(
            isLoggedIn = true,
            didConsentTerms = true,
            analyticsConsent = AnalyticsConsent.ACCEPTED,
            membershipStatus = MembershipStatus.CURRENT,
            canAccessApp = true,
            isInducted = true,
            hasSeenWelcomeScreen = true,
            hasSeenBenefitsScreen = false,
            isAppOnboardingComplete = true,
            shouldShowLandingOptInRecommendations = true
        )

        // THEN the next screen is main activity
        val intent = authFlowManager.navigateFrom(context)
        Assertions.assertThat(intent.component?.className)
            .endsWith(LandingOptInRecommendationsActivity::class.java.simpleName)
    }

    @Test
    fun `if user is a free user, has not seen onboarding welcome screen, show main activity`() {
        mockUserManager(
            isLoggedIn = true,
            didConsentTerms = true,
            analyticsConsent = AnalyticsConsent.ACCEPTED,
            membershipStatus = MembershipStatus.CURRENT,
            canAccessApp = true,
            isInducted = false,
            hasSeenWelcomeScreen = false,
            hasSeenBenefitsScreen = false,
            isAppOnboardingComplete = false,
            subscriptionType = SubscriptionType.NONE
        )

        // THEN the next screen is main activity
        val intent = authFlowManager.navigateFrom(context)
        Assertions.assertThat(intent.component?.className)
            .endsWith(MainActivity::class.java.simpleName)
    }

    fun mockUserManager(
        isLoggedIn: Boolean,
        didConsentTerms: Boolean,
        analyticsConsent: AnalyticsConsent,
        membershipStatus: MembershipStatus,
        canAccessApp: Boolean,
        isInducted: Boolean,
        hasSeenWelcomeScreen: Boolean,
        hasSeenBenefitsScreen: Boolean,
        isAppOnboardingComplete: Boolean,
        isEmailVerified: Boolean = true,
        shouldShowLandingOptInRecommendations: Boolean = false,
        subscriptionType: SubscriptionType = SubscriptionType.EVERY
    ) {
        `when`(userSessionManager.isLoggedIn).thenReturn(isLoggedIn)
        `when`(userManager.didConsentTermsConditions).thenReturn(didConsentTerms)
        `when`(userManager.analyticsConsent).thenReturn(analyticsConsent)
        `when`(userManager.membershipStatus).thenReturn(membershipStatus)
        `when`(userManager.canAccessApp).thenReturn(canAccessApp)
        `when`(userManager.hasSeenOnboardingWelcomeScreen).thenReturn(hasSeenWelcomeScreen)
        `when`(userManager.isInducted).thenReturn(isInducted)
        `when`(userManager.subscriptionType).thenReturn(subscriptionType)
        `when`(userManager.isAppOnboardingComplete).thenReturn(isAppOnboardingComplete)
        `when`(userManager.accountId).thenReturn(testAccountId)
        `when`(userManager.isEmailVerified).thenReturn(isEmailVerified)
        `when`(userManager.shouldShowLandingOptInRecommendations).thenReturn(
            shouldShowLandingOptInRecommendations
        )
    }
}