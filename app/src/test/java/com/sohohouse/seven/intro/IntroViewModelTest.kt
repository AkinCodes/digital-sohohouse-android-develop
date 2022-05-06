package com.sohohouse.seven.intro

import android.content.Context
import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.sohohouse.seven.authentication.AuthenticationFlowManager
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.EmptyStringProvider
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.utils.TestCoroutineRule
import com.sohohouse.seven.common.utils.ZipRequestsUtil
import com.sohohouse.seven.intro.adapter.*
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.models.*
import com.sohohouse.seven.network.core.request.PatchMembershipAttributesRequest
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

@ExperimentalCoroutinesApi
class IntroViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @MockK
    lateinit var context: Context

    @MockK
    lateinit var accountInteractor: AccountInteractor

    @MockK
    lateinit var analyticsManager: AnalyticsManager

    @MockK
    lateinit var userManager: UserManager

    @MockK
    lateinit var flowManager: AuthenticationFlowManager

    @MockK
    lateinit var zipRequestsUtil: ZipRequestsUtil

    private val stringProvider: StringProvider = EmptyStringProvider()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `Return introductory items for Every Membership`() = runBlockingTest {
        every { accountInteractor.getCompleteAccountV2() } returns
                Either.Value(mockAccountWithSubscriptionType(SubscriptionType.EVERY))
        every { userManager.subscriptionType } returns SubscriptionType.EVERY
        every { userManager.isStaff } returns false

        val observer = mockk<Observer<List<IntroItem>>>()
        val slot = slot<List<IntroItem>>()
        val items = mutableListOf<IntroItem>()
        every { observer.onChanged(capture(slot)) } answers {
            items.addAll(slot.captured)
        }

        with(getViewModel()) {
            this.items.observeForever(observer)
            this.getUserProfile()
        }

        assert(items.size == 1)
        assert(items.first() is IntroLanding)
    }

    @Test
    fun `Return introductory items for Friends Membership`() = runBlockingTest {
        every { accountInteractor.getCompleteAccountV2() } returns
                Either.Value(mockAccountWithSubscriptionType(SubscriptionType.FRIENDS))
        every { userManager.subscriptionType } returns SubscriptionType.FRIENDS
        every { userManager.isStaff } returns false

        val observer = mockk<Observer<List<IntroItem>>>()
        val slot = slot<List<IntroItem>>()
        val items = mutableListOf<IntroItem>()
        every { observer.onChanged(capture(slot)) } answers {
            items.addAll(slot.captured)
        }

        with(getViewModel()) {
            this.items.observeForever(observer)
            this.getUserProfile()
        }

        assert(items.size == 4)
        assert(items.first() is IntroLanding)
        assert(items.contains(StayWithUs))
        assert(items.contains(SpacesForFriends))
        assert(items.contains(MemberBenefits))
    }

    @Test
    fun `Patch membership when Friends Membership completes Introductory screens`() =
        runBlockingTest {
            every { accountInteractor.getCompleteAccountV2() } returns
                    Either.Value(mockAccountWithSubscriptionType(SubscriptionType.FRIENDS))
            every { userManager.subscriptionType } returns SubscriptionType.FRIENDS
            every { userManager.isStaff } returns false
            every { userManager setProperty "isAppOnboardingNeeded" value any<Boolean>() } just Runs
            every { userManager setProperty "hasSeenOnboardingWelcomeScreen" value any<Boolean>() } just Runs
            every { userManager setProperty "hasSeenOnboardingBenefitsScreen" value any<Boolean>() } just Runs
            every { userManager setProperty "isInducted" value any<Boolean>() } just Runs
            every { zipRequestsUtil.issueApiCall(any<PatchMembershipAttributesRequest>()) } returns Either.Value(
                Membership(inductedAt = Date())
            )
            every { flowManager.navigateFrom(context) } returns Intent()

            val observer = mockk<Observer<Intent>>()
            val slot = slot<Intent>()
            var intent: Intent? = null
            every { observer.onChanged(capture(slot)) } answers {
                intent = slot.captured
            }

            with(getViewModel()) {
                this.intent.observeForever(observer)
                this.onCompleteIntro(context)
            }

            verify { zipRequestsUtil.issueApiCall(any<PatchMembershipAttributesRequest>()) }
            assert(intent != null)
        }

    private fun getViewModel(): IntroViewModel {
        return IntroViewModel(
            accountInteractor,
            userManager,
            stringProvider,
            flowManager,
            zipRequestsUtil,
            analyticsManager,
            Dispatchers.Unconfined
        )
    }

    private fun mockAccountWithSubscriptionType(subscriptionType: SubscriptionType): Account {
        return mockk<Account>().also {
            every { it.id } answers { "" }
            every { it.loyaltyId } answers { "" }
            every { it.shortCode } answers { "" }
            every { it.profile } answers {
                mockk<Profile>().also {
                    every { it.firstName } answers { "" }
                    every { it.lastName } answers { "" }
                    every { it.imageUrl } answers { "" }
                    every { it.isStaff } answers { false }
                }
            }
            every { it.membership } answers {
                mockk<Membership>().also {
                    every { it.subscriptionType } answers { subscriptionType.name }
                    every { it.code } answers { "" }
                }
            }
            every { it.localHouse } answers {
                mockk<Venue>().also {
                    every { it.name } answers { "" }
                    every { it.venueIcons } answers {
                        VenueIcons("", "", "", "")
                    }
                }
            }
        }
    }
}