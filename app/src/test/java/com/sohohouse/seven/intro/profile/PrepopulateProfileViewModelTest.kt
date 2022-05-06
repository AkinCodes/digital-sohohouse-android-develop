package com.sohohouse.seven.intro.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.sohohouse.seven.authentication.AuthenticationFlowManager
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.firstName
import com.sohohouse.seven.common.extensions.imageUrl
import com.sohohouse.seven.common.relaxedMockk
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.TestCoroutineRule
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.profile.IndustriesRepository
import com.sohohouse.seven.profile.ProfileField
import com.sohohouse.seven.profile.ProfileRepository
import com.sohohouse.seven.profile.ProfileTestHelper
import io.mockk.CapturingSlot
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class PrepopulateProfileViewModelTest {
    @MockK(relaxed = true)
    lateinit var analyticsManager: AnalyticsManager

    @MockK
    lateinit var profileRepo: ProfileRepository

    @MockK
    lateinit var industriesRepo: IndustriesRepository

    @MockK
    lateinit var flowManager: AuthenticationFlowManager

    @MockK(relaxed = true)
    lateinit var userManager: UserManager

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `viewmodel emits editing state on init, then profile data`() = runBlockingTest {
        val account = ProfileTestHelper.fullMockProfileAccount()
        every { profileRepo.getMyAccountWithProfileV2() } returns value(account)
        val viewModel = PrepopulateProfileViewModel(
            analyticsManager,
            flowManager,
            profileRepo,
            industriesRepo,
            userManager,
            Dispatchers.Unconfined
        )

        val stateObserver = relaxedMockk<Observer<PrepopulateProfileViewModel.UiState>>()
        viewModel.state.observeForever(stateObserver)

        val fieldsObserer = relaxedMockk<Observer<List<ProfileField<*>>>>()
        viewModel.fields.observeForever(fieldsObserer)

        val nameObserver = relaxedMockk<Observer<String>>()
        viewModel.profileName.observeForever(nameObserver)

        val imageObserver = relaxedMockk<Observer<String>>()
        viewModel.profileImage.observeForever(imageObserver)

        val errorObserver = relaxedMockk<Observer<Any>>()
        viewModel.errorViewState.observeForever(errorObserver)

        val stateCaptor = CapturingSlot<PrepopulateProfileViewModel.UiState>()
        val fieldsCaptor = CapturingSlot<List<ProfileField<*>>>()
        val nameCaptor = CapturingSlot<String>()
        val imageCaptor = CapturingSlot<String>()

        verify(exactly = 0) { errorObserver.onChanged(any()) }
        verify { stateObserver.onChanged(capture(stateCaptor)) }
        verify { fieldsObserer.onChanged(capture(fieldsCaptor)) }
        verify { nameObserver.onChanged(capture(nameCaptor)) }
        verify { imageObserver.onChanged(capture(imageCaptor)) }

        assertEquals(PrepopulateProfileViewModel.UiState.EDITING, stateCaptor.captured)
        assertEquals(account.firstName, nameCaptor.captured)
        assertEquals(account.imageUrl, imageCaptor.captured)
        assertEquals(3, fieldsCaptor.captured.size)
        assertEquals(ProfileField.Occupation::class.java, fieldsCaptor.captured[0].javaClass)
        assertEquals(ProfileField.Industry::class.java, fieldsCaptor.captured[1].javaClass)
        assertEquals(ProfileField.City::class.java, fieldsCaptor.captured[2].javaClass)
        assertEquals(
            account.profile?.occupation,
            (fieldsCaptor.captured[0] as ProfileField.Occupation).data
        )
        assertEquals(
            account.profile?.industry,
            (fieldsCaptor.captured[1] as ProfileField.Industry).data?.value
        )
        assertEquals(account.profile?.city, (fieldsCaptor.captured[2] as ProfileField.City).data)
    }

    @Test
    fun `on proceed btn click while in editing state viewmodel saves profile then emits completed state`() =
        runBlockingTest {
            val account = ProfileTestHelper.fullMockProfileAccount()
            every { profileRepo.getMyAccountWithProfileV2() } returns value(account)
            every { profileRepo.saveProfileWithAccountUpdateV2(any(), any()) } returns value(Any())
            val viewModel = PrepopulateProfileViewModel(
                analyticsManager,
                flowManager,
                profileRepo,
                industriesRepo,
                userManager,
                Dispatchers.Unconfined
            )

            val stateObserver = relaxedMockk<Observer<PrepopulateProfileViewModel.UiState>>()
            val stateSlot = ArrayList<PrepopulateProfileViewModel.UiState>()
            viewModel.state.observeForever(stateObserver)

            viewModel.onProceedBtnClick(relaxedMockk())

            verify { profileRepo.saveProfileWithAccountUpdateV2(any(), any()) }
            verify { stateObserver.onChanged(capture(stateSlot)) }
            assertEquals(PrepopulateProfileViewModel.UiState.COMPLETED, stateSlot.last())
        }
}