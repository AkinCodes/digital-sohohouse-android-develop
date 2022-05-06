package com.sohohouse.seven.more.profile.crop.edit

import android.app.Activity
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseFragment
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.databinding.FragmentCropEditBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class CropEditFragment : BaseFragment() {

    companion object {
        private const val WIDTH_PX = 328
        private const val HEIGHT_PX = 328
        const val ARGS_UNCROPPED_IMAGE_URI_KEY = "CropEditFragment.uncropped_image_uri"
    }

    override val contentLayoutId get() = R.layout.fragment_crop_edit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uncroppedUri = arguments?.getParcelable(ARGS_UNCROPPED_IMAGE_URI_KEY) as? Uri
        FragmentCropEditBinding.bind(view).setupViews(uncroppedUri)
    }

    fun FragmentCropEditBinding.setupViews(uncroppedUri: Uri?) {
        with(toolbar) {
            toolbarTitle.setText(R.string.more_change_photo_upload_cta)
            toolbarBackBtn.clicks { requireActivity().onBackPressed() }
        }

        uncroppedUri?.let { uri ->
            with(cropPreviewCiv) {
                setImageUriAsync(uri)
                setOnSetImageUriCompleteListener { _, imageUri, error ->
                    logError(imageUri, error)
                }
            }

            cropPreviewCropBtn.clicks {
                cropImageAndOpenPreview(it)
            }

            cropPreviewCancelBtn.clicks {
                requireActivity().apply {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            }
        }
    }

    private fun logError(imageUri: Uri, error: Exception?) {
        error?.let { Timber.d("uri ($imageUri) set error: ${it.message}") }
    }

    private fun FragmentCropEditBinding.cropImageAndOpenPreview(view: View) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            FirebaseCrashlytics.getInstance().recordException(throwable)
        }

        lifecycleScope.launch(Dispatchers.Default + coroutineExceptionHandler) {
            val croppedImage = cropPreviewCiv.getCroppedImage(WIDTH_PX, HEIGHT_PX)
                ?: BitmapFactory.decodeResource(resources, R.drawable.ic_profile)

            withContext(Dispatchers.Main) {
                val action =
                    CropEditFragmentDirections.navigateFromCropEditFragmentToCropPreviewFragment(
                        croppedImage
                    )
                findNavController().navigate(action)
            }
        }
    }

}