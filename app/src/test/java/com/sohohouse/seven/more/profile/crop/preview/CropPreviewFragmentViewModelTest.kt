package com.sohohouse.seven.more.profile.crop.preview

import android.graphics.Bitmap
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth
import com.sohohouse.seven.app.TestApp
import com.sohohouse.seven.common.utils.TestCoroutineRule
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.models.Profile
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private val fakeProfile = Profile(
    _firstName = "Obi-Wan",
    _lastName = "Kenobi",
    occupation = "Jedi",
    _imageUrl = "https://fake_image_url"
).apply {
    id = "Fake_Profile_ID"
}

@MediumTest
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, application = TestApp::class)
class CropPreviewFragmentViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val globalTimeout: Timeout = Timeout.seconds(20)

    @MockK(relaxed = true)
    lateinit var interactor: ProfileImageCropperInteractor

    private lateinit var viewModel: CropPreviewFragmentViewModel

    private lateinit var fakeBitmap: Bitmap

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = CropPreviewFragmentViewModel(
            analyticsManager = mockk(relaxed = true),//we do not need this, so we can mock locally.
            dispatcher = testCoroutineRule.testCoroutineDispatcher,
            firebaseEventTracking = mockk(relaxed = true),//we do not need this, so we can mock locally.
            imageCropperInteractor = interactor
        )

        fakeBitmap = Bitmap.createBitmap(100, 200, Bitmap.Config.ARGB_8888)
    }

    @Test
    fun `test successful upload of image`() = runBlockingTest {
        coEvery {
            interactor.uploadImage(fakeBitmap)
        } returns Either.Value(fakeProfile)

        viewModel.uploadNewProfileImage(fakeBitmap)

        val result = viewModel.newProfileUrl.first()

        Truth.assertThat(result).isEqualTo(fakeProfile.imageUrl)
    }

    @Test
    fun `test when image upload returns error`() = runBlockingTest {
        val viewModel = spyk(viewModel)

        coEvery {
            interactor.uploadImage(fakeBitmap)
        } returns Either.Error(ServerError.TIMEOUT)

        viewModel.uploadNewProfileImage(fakeBitmap)

        val result = viewModel.newProfileUrl.first()

        Truth.assertThat(result).isEmpty()
        verify(exactly = 1) { viewModel.handleError(ServerError.TIMEOUT) }
    }

    @Test
    fun `test when image upload returns empty result`() = runBlockingTest {
        val viewModel = spyk(viewModel)

        coEvery {
            interactor.uploadImage(fakeBitmap)
        } returns Either.Empty()

        viewModel.uploadNewProfileImage(fakeBitmap)

        val result = viewModel.newProfileUrl.first()

        Truth.assertThat(result).isEmpty()
        verify(exactly = 0) { viewModel.handleError(ServerError.TIMEOUT) }
    }

}