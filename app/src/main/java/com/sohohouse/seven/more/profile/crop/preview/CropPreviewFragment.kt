package com.sohohouse.seven.more.profile.crop.preview

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.ErrorDialogViewController
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.utils.collectLatest
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.databinding.FragmentCropPreviewBinding

class CropPreviewFragment : BaseMVVMFragment<CropPreviewFragmentViewModel>(), Loadable.View,
    ErrorDialogViewController {

    override val contentLayoutId get() = R.layout.fragment_crop_preview

    override val viewModelClass = CropPreviewFragmentViewModel::class.java

    override var loadingView: LoadingView? = null

    private val args: CropPreviewFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentCropPreviewBinding.bind(view).setupViews()
        setupObservables()
    }

    private fun FragmentCropPreviewBinding.setupViews() {
        args.croppedImage?.let { bitmap ->
            cropResultIv.setImageBitmap(bitmap)

            cropResultUploadBtn.clicks {
                viewModel.uploadNewProfileImage(bitmap)
            }
        }
        cropResultCancelBtn.clicks {
            requireActivity().apply {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
        loadingView = cropResultLv
    }

    private fun setupObservables() {
        viewModel.newProfileUrl.collectLatest(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                onPhotoUploaded(it)
            }
        }

        observeLoadingState(viewLifecycleOwner) {
            loadingView?.toggleSpinner(it == LoadingState.Loading)
        }

        observeErrorDialogEvents()
    }

    private fun onPhotoUploaded(imageUrl: String) {
        requireActivity().apply {
            setResult(Activity.RESULT_OK, Intent().apply { data = Uri.parse(imageUrl) })
            finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loadingView = null
    }

}