package com.sohohouse.seven.more

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.sohohouse.seven.BuildConfigManager
import com.sohohouse.seven.FeatureFlags
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.LogoutUtil
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.Account
import com.sohohouse.seven.network.core.models.Feature
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.profile.ProfileRepository
import com.sohohouse.seven.profile.ProfileTestHelper
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import moe.banana.jsonapi2.Document
import moe.banana.jsonapi2.HasMany
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AccountViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    lateinit var profileRepo: ProfileRepository

    @MockK
    lateinit var analyticsManager: AnalyticsManager

    @MockK
    lateinit var logoutUtil: LogoutUtil

    @MockK
    lateinit var userManager: UserManager

    @MockK
    lateinit var buildConfigManager: BuildConfigManager


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        ProfileTestHelper.fullMockProfile()
        every { buildConfigManager.isCurrentlyStaging } returns false
    }

    @Test
    fun `when user has non friends membership, viewmodel returns guest invitations menu`() =
        runBlockingTest {
            every { userManager.subscriptionType } returns SubscriptionType.EVERY
            coEvery { profileRepo.getMyAccountWithProfileV2() } returns value(
                createAccount(
                    canInviteGuests = true
                )
            )

            val viewModel = AccountViewModel(
                profileRepo,
                AccountViewInfo(userManager),
                logoutUtil,
                userManager,
                analyticsManager
            )

            val items = viewModel.getViewInfo()
            Assert.assertTrue("Menu items emitted includes guest invitations item",
                items.any { item: AccountMenu -> item == AccountMenu.GUEST_INVITATIONS })
        }

    @Test
    fun `when fetching user account, viewmodel emits profile data`() = runBlockingTest {
        every { userManager.subscriptionType } returns SubscriptionType.EVERY
        coEvery { profileRepo.getMyAccountWithProfileV2() } returns value(
            createAccount(
                canInviteGuests = true
            )
        )

        val viewModel = AccountViewModel(
            profileRepo,
            AccountViewInfo(userManager),
            logoutUtil,
            userManager,
            analyticsManager,
            Dispatchers.Unconfined
        )

        val observer = mockk<Observer<ProfileItem>>(relaxed = true)
        viewModel.profile.observeForever(observer)

        verify(exactly = 1) { observer.onChanged(any()) }
    }

    private fun createAccount(canInviteGuests: Boolean): Account {
        return mockk<Account>().apply {
            every { features } returns if (canInviteGuests)
                HasMany(Feature().apply { id = FeatureFlags.Ids.FEATURE_ID_GUEST_REGISTRATION })
            else
                HasMany()
            every { profile } returns Profile()
            every { profile?.id } returns "profile_id"
            every { profile?.firstName } returns ""
            every { profile?.lastName } returns ""
            every { profile?.occupation } returns ""
            every { profile?.city } returns ""
            every { profile?.imageUrl } returns ""
            every { document } returns object : Document() {}
            every { profile?.socialsOptIn } returns false
            every { profile?.isStaff } returns false
            every { profile?.pronouns } returns emptyList()
        }
    }
}