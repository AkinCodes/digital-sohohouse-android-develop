package com.sohohouse.seven.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth
import com.sohohouse.seven.base.error.ErrorHelper
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.TestCoroutineRule
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.empty
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.base.model.error
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.profile.ProfileRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout

@ExperimentalCoroutinesApi
@SmallTest
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val globalTimeout: Timeout = Timeout.seconds(10)

    @MockK(relaxed = true)
    private lateinit var userManager: UserManager

    @MockK(relaxed = true)
    private lateinit var profileRepo: ProfileRepository

    private lateinit var viewModel: MainViewModel

    private val fakeProfileID = "Fake_Profile_ID_1"
    private val fakeProfile =
        Profile(_firstName = "Obi-Wan", _lastName = "Kenobi", occupation = "Jedi")
    private lateinit var fakeProfileItem: ProfileItem

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        fakeProfile.id = fakeProfileID
        fakeProfileItem = ProfileItem(fakeProfile)
        every { userManager.subscriptionType } returns SubscriptionType.FRIENDS

        val tmpViewModel = MainViewModel(
            mockk(relaxed = true),
            mockk(relaxed = true),
            userManager,
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            profileRepo,
            testCoroutineRule.testCoroutineDispatcher,
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
        )

        viewModel = spyk(tmpViewModel, recordPrivateCalls = true)

        every { viewModel["loadLongUrl"](allAny<String>()) } returns "FakeShortProfileUrl/$fakeProfileID"
    }

    @Test
    fun `load profile with short url`() = runBlockingTest {

        every { profileRepo.getProfile(fakeProfileID) } returns value(fakeProfile)

        val deferred = async {
            viewModel.sharedProfile.first()
        }

        viewModel.loadProfile("fakeUrl")

        Truth.assertThat(deferred.await()).isEqualTo(fakeProfileItem)
    }

    @Test
    fun `load profile with short url returns error`() = runBlockingTest {

        every { profileRepo.getProfile(fakeProfileID) } returns error(ServerError.INVALID_RESPONSE)

        val deferred = async {
            viewModel.sharedProfileState.first()
        }

        viewModel.loadProfile("fakeUrl")

        Truth.assertThat(deferred.await()).isEqualTo(ErrorHelper.ERROR_LOADING_SHARED_PROFILE)
    }

    @Test
    fun `load profile with short url returns empty data`() = runBlockingTest {

        every { profileRepo.getProfile(fakeProfileID) } returns empty()

        val deferred = async {
            viewModel.sharedProfileState.first()
        }

        viewModel.loadProfile("fakeUrl")

        Truth.assertThat(deferred.await()).isEqualTo(ErrorHelper.EMPTY_PROFILE)
    }


}