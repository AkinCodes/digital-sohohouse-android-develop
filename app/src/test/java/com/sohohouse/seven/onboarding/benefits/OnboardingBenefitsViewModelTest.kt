package com.sohohouse.seven.onboarding.benefits

import android.content.Context
import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.verify
import com.sohohouse.seven.app.TestApp
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.common.mock
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.EmptyStringProvider
import com.sohohouse.seven.main.MainActivity
import com.sohohouse.seven.memberonboarding.induction.booking.InductionBookingActivity
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.models.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApp::class)
@ExperimentalCoroutinesApi
class OnboardingBenefitsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var accountInteractor: AccountInteractor

    @Mock
    lateinit var analyticsManager: AnalyticsManager

    @Mock
    lateinit var userManager: UserManager

    @Mock
    lateinit var context: Context

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `Get benefits for users without membership and verify no access`() = runBlockingTest {
        val items = runGetBenefitsTest(SubscriptionType.NONE)
        assert(items.isEmpty())
    }

    @Test
    fun `Get benefits for friends member and verify limited access`() = runBlockingTest {
        val items = runGetBenefitsTest(SubscriptionType.FRIENDS)
        assert(items.size == 7
                && items.count { it is MembershipCardItem } == 1
                && items.count { it is BenefitHeaderItem } == 1
                && items.count { it is BenefitItem.FoodBeverage } == 1
                && items.count { it is BenefitItem.Spa } == 1
                && items.count { it is BenefitItem.SohoHome } == 1
                && items.count { it is BenefitItem.Events } == 1
                && items.count { it is BenefitItem.RoomBookings } == 1
        )
    }

    @Test
    fun `Get benefits for house member of CWH (cities without house) and verify full access`() =
        runBlockingTest {
            val items = runGetBenefitsTest(SubscriptionType.CWH)
            assert(verifyFullAccess(items))
        }

    @Test
    fun `Get benefits for house member of LOCAL and verify full access`() = runBlockingTest {
        val items = runGetBenefitsTest(SubscriptionType.LOCAL)
        assert(verifyFullAccess(items))
    }

    @Test
    fun `Get benefits for house member of EVERY and verify full access`() = runBlockingTest {
        val items = runGetBenefitsTest(SubscriptionType.EVERY)
        assert(verifyFullAccess(items))
    }

    @Test
    fun `Get benefits for house member of EVERY_PLUS and verify full access`() = runBlockingTest {
        val items = runGetBenefitsTest(SubscriptionType.EVERY_PLUS)
        assert(verifyFullAccess(items))
    }

    @Test
    fun `Friends member clicks Continue button and lands on main page`() = runBlockingTest {
        val intent = runContinueClickTest(SubscriptionType.FRIENDS)
        assertEquals(intent.component?.className, MainActivity::class.java.name)
    }

    @Test
    fun `CWH member clicks Continue button and lands on main page`() = runBlockingTest {
        val intent = runContinueClickTest(SubscriptionType.CWH)
        assertEquals(intent.component?.className, MainActivity::class.java.name)
    }

    @Test
    fun `Local member clicks Continue button and lands on house induction page`() =
        runBlockingTest {
            val intent = runContinueClickTest(SubscriptionType.LOCAL)
            assertEquals(intent.component?.className, InductionBookingActivity::class.java.name)
        }

    @Test
    fun `Every member clicks Continue button and lands on house induction page`() =
        runBlockingTest {
            val intent = runContinueClickTest(SubscriptionType.EVERY)
            assertEquals(intent.component?.className, InductionBookingActivity::class.java.name)
        }

    @Test
    fun `Every Plus member clicks Continue button and lands on house induction page`() =
        runBlockingTest {
            val intent = runContinueClickTest(SubscriptionType.EVERY_PLUS)
            assertEquals(intent.component?.className, InductionBookingActivity::class.java.name)
        }

    private fun runContinueClickTest(subscriptionType: SubscriptionType): Intent {
        val viewModel = prepareViewModel()
        val observer = mock<Observer<Intent>>()
        viewModel.navigation.observeForever(observer)

        mockUserAccountResponse(subscriptionType)

        viewModel.onClickContinue(context)

        val captor = ArgumentCaptor.forClass(Intent::class.java)
        verify(observer).onChanged(captor.capture())

        return captor.value
    }

    private fun runGetBenefitsTest(subscriptionType: SubscriptionType): List<BenefitAdapterItem> {
        val viewModel = prepareViewModel()
        val observer = mock<Observer<List<BenefitAdapterItem>>>()
        viewModel.items.observeForever(observer)

        mockUserAccountResponse(subscriptionType = subscriptionType)

        viewModel.getBenefits()

        val captor =
            ArgumentCaptor.forClass(List::class.java) as ArgumentCaptor<List<BenefitAdapterItem>>
        verify(observer).onChanged(captor.capture())

        return captor.value
    }

    private fun mockUserAccountResponse(subscriptionType: SubscriptionType) {
        val userAccount = mockk<Account>().also { account ->
            every { account.membership?.subscriptionType } returns subscriptionType.name
//            every { account.subscriptionType } returns subscriptionType
//            every { account.localHouse.name } returns ""
//            every { account.membershipDisplayName } returns 0
//            every { account.id } returns "1234567"
//            every { account.loyaltyId } returns "test"
//            every { account.isStaff } returns false
            every { account.localHouse } returns Venue()
            every { account.membership?.code } returns subscriptionType.name
            every { account.profile } returns Profile()
            every { account.id } returns "test"
            every { account.loyaltyId } returns "test"
            every { account.shortCode } returns ""
        }
        `when`(accountInteractor.getCompleteAccountV2()).thenReturn(Either.Value(userAccount))
    }

    private fun verifyFullAccess(items: List<BenefitAdapterItem>): Boolean {
        return items.size == 7
                && items.count { it is MembershipCardItem } == 1
                && items.count { it is BenefitHeaderItem } == 1
                && items.count { it is BenefitItem.Houses } == 1
                && items.count { it is BenefitItem.Events } == 1
                && items.count { it is BenefitItem.RoomBookings } == 1
                && items.count { it is BenefitItem.WellBeing } == 1
                && items.count { it is BenefitItem.FoodBeverage } == 1
    }

    private fun prepareViewModel(): OnboardingBenefitsViewModel {
        return OnboardingBenefitsViewModel(
            accountInteractor,
            userManager,
            analyticsManager,
            Dispatchers.Unconfined,
            EmptyStringProvider()
        )
    }
}