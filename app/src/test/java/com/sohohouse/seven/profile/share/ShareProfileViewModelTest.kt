package com.sohohouse.seven.profile.share

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.sohohouse.seven.app.TestApp
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.TestCoroutineRule
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.models.ShortProfileUrlResponse
import com.sohohouse.seven.profile.ProfileRepository
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private const val FAKE_PROFILE_ID = "FakeProfileID"
private const val FAKE_SHORT_PROFILE_URL = "https://sh.app/r/FakeUrl"
private const val FAKE_ID = "1ff6a25d-f026-4e02-b260-2228dfd2b5fc"

@ExperimentalCoroutinesApi
@SmallTest
@RunWith(RobolectricTestRunner::class) //Need to mock android environment, to use android.text.TextUtils.isEmpty() function
@Config(manifest = Config.NONE, application = TestApp::class)
class ShareProfileViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val globalTimeout: Timeout = Timeout.seconds(15)

    @MockK
    lateinit var userManager: UserManager

    @MockK
    lateinit var sohoApiService: SohoApiService

    private lateinit var profileRepo: ProfileRepository

    private lateinit var viewModel: ShareProfileViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { userManager.profileID } returns FAKE_PROFILE_ID

        profileRepo = ProfileRepository(
            coreRequestFactory = mockk(relaxed = true),
            userManager = userManager,
            sohoApiService = sohoApiService
        )
    }

    /**
     * Some calls need to be mocked before ViewModel creation, that is why this call is not in @Before function
     */
    private fun setupViewModel() {
        viewModel = ShareProfileViewModel(
            profileRepo,
            userManager,
            analyticsManager = mockk(relaxed = true),//we do not need analyticsManager, so we can mock locally.
            testCoroutineRule.testCoroutineDispatcher
        )
    }

    @Test
    fun `load valid short profile url`() = runBlockingTest {
        coEvery { sohoApiService.createShortProfilesUrls(any()) } returns ApiResponse.Success(
            ShortProfileUrlResponse().apply {
                shortUrl = FAKE_SHORT_PROFILE_URL
                id = FAKE_ID //fake id is need to save value in MutableStateFlow
            })

        setupViewModel()

        val result = viewModel.shortProfileUrl.first()

        assertThat(result.shortUrl).isEqualTo(FAKE_SHORT_PROFILE_URL)
        assertThat(result.id).isEqualTo(FAKE_ID)
    }

    @Test
    fun `load invalid short profile url`() = runBlockingTest {
        coEvery { sohoApiService.createShortProfilesUrls(any()) } returns ApiResponse.Error()

        setupViewModel()

        val result = viewModel.shortProfileUrl.first()

        assertThat(result.shortUrl).isEqualTo("")
        assertThat(result.id).isEqualTo(null)
    }

    @Test
    fun `load empty short profile url`() = runBlockingTest {
        coEvery { sohoApiService.createShortProfilesUrls(any()) } returns ApiResponse.Success(
            ShortProfileUrlResponse()
        )

        setupViewModel()

        val result = viewModel.shortProfileUrl.first()

        assertThat(result.shortUrl).isEqualTo("")
        assertThat(result.id).isEqualTo(null)
    }

    @Test
    fun `load already existed short profile url`() = runBlockingTest {
        profileRepo = mockk(relaxed = true)
        every { profileRepo.shortProfileUrl } returns MutableStateFlow(ShortProfileUrlResponse().apply {
            shortUrl = FAKE_SHORT_PROFILE_URL
            id = FAKE_ID
        })

        setupViewModel()

        coVerify(exactly = 0) { profileRepo.loadShortProfileUrl(FAKE_PROFILE_ID) }

        val result = viewModel.shortProfileUrl.first()

        assertThat(result.shortUrl).isEqualTo(FAKE_SHORT_PROFILE_URL)
        assertThat(result.id).isEqualTo(FAKE_ID)
    }

}