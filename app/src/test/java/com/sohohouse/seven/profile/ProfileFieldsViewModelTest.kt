package com.sohohouse.seven.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.contains
import com.sohohouse.seven.common.prefs.PrefsManager
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.EmptyStringProvider
import com.sohohouse.seven.common.utils.TestCoroutineRule
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.profile.view.ProfileFieldsViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ProfileFieldsViewModelTest {

    companion object {
        private const val MY_PROFILE_ID = "myProfID"
        private const val OTHER_USER_PROFILE_ID = "otherProfID"
    }

    @MockK
    private lateinit var profileRepo: ProfileRepository

    @MockK
    private lateinit var userManager: UserManager

    @MockK
    private lateinit var prefsManager: PrefsManager

    @MockK
    lateinit var analyticsManager: AnalyticsManager

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `on set my profile ID on viewmodel it retrieves my profile and if successful emits expected values`() =
        runBlockingTest {
            val profile = ProfileTestHelper.fullMockProfile()

            coEvery { profileRepo.getMyProfile() } returns Either.Value(profile)
            every { userManager.profileID } returns MY_PROFILE_ID
            every { prefsManager.notificationsCustomised } returns true

            val viewModel = ProfileFieldsViewModel(
                profileRepo,
                userManager,
                prefsManager,
                EmptyStringProvider(),
                MY_PROFILE_ID,
                analyticsManager
            )

            val dataObserver = mockk<Observer<List<DiffItem>>>(relaxed = true)
            viewModel.profileListItems.observeForever(dataObserver)

            val errorObserver = mockk<Observer<Any>>(relaxed = true)
            viewModel.error.observeForever(errorObserver)

            viewModel.fetchData()

            verify(exactly = 1) { profileRepo.getMyProfile() }
            verify(exactly = 1) { dataObserver.onChanged(any()) }
            verify(exactly = 0) { errorObserver.onChanged(any()) }
        }

    @Test
    fun `on set other profile ID on viewmodel it retrieves other profile and if successful emits expected values`() =
        runBlockingTest {
            val profile = ProfileTestHelper.fullMockProfile()

            coEvery { profileRepo.getProfile(OTHER_USER_PROFILE_ID) } returns Either.Value(profile)
            every { userManager.profileID } returns MY_PROFILE_ID

            val viewModel = ProfileFieldsViewModel(
                profileRepo,
                userManager,
                prefsManager,
                EmptyStringProvider(),
                OTHER_USER_PROFILE_ID,
                analyticsManager
            )

            val dataObserver = mockk<Observer<List<DiffItem>>>(relaxed = true)
            viewModel.profileListItems.observeForever(dataObserver)

            val errorObserver = mockk<Observer<Any>>(relaxed = true)
            viewModel.error.observeForever(errorObserver)

            viewModel.fetchData()

            verify(exactly = 1) { profileRepo.getProfile(OTHER_USER_PROFILE_ID) }
            verify(exactly = 1) { dataObserver.onChanged(any()) }
            verify(exactly = 0) { errorObserver.onChanged(any()) }
        }

    @Test
    fun `on init viewmodel retrieves profile and if unsuccessful emits expected values`() =
        runBlockingTest {
            coEvery { profileRepo.getMyProfile() } returns Either.Error(ServerError.BAD_REQUEST)
            every { userManager.profileID } returns MY_PROFILE_ID
            every { prefsManager.notificationsCustomised } returns false

            val viewModel = ProfileFieldsViewModel(
                profileRepo,
                userManager,
                prefsManager,
                EmptyStringProvider(),
                MY_PROFILE_ID,
                analyticsManager
            )

            val dataObserver = mockk<Observer<List<DiffItem>>>(relaxed = true)
            viewModel.profileListItems.observeForever(dataObserver)

            val errorObserver = mockk<Observer<String>>(relaxed = true)
            viewModel.error.observeForever(errorObserver)

            viewModel.fetchData()

            verify(exactly = 1) { profileRepo.getMyProfile() }
            verify(exactly = 0) { dataObserver.onChanged(any()) }
            verify(exactly = 1) { errorObserver.onChanged(any()) }
        }

    @Test
    fun `viewmodel emits complete profile module when viewing my profile`() = runBlockingTest {
        val profile = ProfileTestHelper.emptyProfile()
        coEvery { profileRepo.getMyProfile() } returns Either.Value(profile)
        every { userManager.profileID } returns profile.id
        every { prefsManager.notificationsCustomised } returns false

        val viewModel = ProfileFieldsViewModel(
            profileRepo,
            userManager,
            prefsManager,
            EmptyStringProvider(),
            profile.id,
            analyticsManager
        )

        val dataObserver = mockk<Observer<List<DiffItem>>>(relaxed = true)
        viewModel.profileListItems.observeForever(dataObserver)

        viewModel.fetchData()

        CapturingSlot<List<DiffItem>>().let { slot ->
            verify(exactly = 1) { dataObserver.onChanged(capture(slot)) }
            assert(slot.captured.contains { it is BaseAdapterItem.SetUpAppPromptItem.Container })
        }
    }

    @Test
    fun `viewmodel does not emit complete profile module when viewing another profile`() {
        val profile = ProfileTestHelper.emptyProfile()
        coEvery { profileRepo.getProfile(profile.id) } returns Either.Value(profile)
        every { userManager.profileID } returns OTHER_USER_PROFILE_ID
        every { prefsManager.notificationsCustomised } returns true

        val viewModel = ProfileFieldsViewModel(
            profileRepo,
            userManager,
            prefsManager,
            EmptyStringProvider(),
            profile.id,
            analyticsManager
        )

        val dataObserver = mockk<Observer<List<DiffItem>>>(relaxed = true)
        viewModel.profileListItems.observeForever(dataObserver)

        viewModel.fetchData()

        CapturingSlot<List<DiffItem>>().let { slot ->
            verify { dataObserver.onChanged(capture(slot)) }
            assert(slot.captured.none { it is BaseAdapterItem.SetUpAppPromptItem.Container })
        }
    }

}
