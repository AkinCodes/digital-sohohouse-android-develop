package com.sohohouse.seven.more.profile.crop

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseBottomSheet
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.databinding.FragmentImageSourceChooserBinding

class ImageSourceChooserBottomSheet : BaseBottomSheet() {
    override val contentLayout: Int
        get() = R.layout.fragment_image_source_chooser

    override val fixedHeight: Int = ViewGroup.LayoutParams.WRAP_CONTENT

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentImageSourceChooserBinding.bind(view).initView()
    }

    private fun FragmentImageSourceChooserBinding.initView() {
        cameraBtn.setOnClickListener { setResultByAction(ACTION_CAMERA) }
        galleryBtn.setOnClickListener { setResultByAction(ACTION_GALLERY) }
        cancelBtn.setOnClickListener { dismiss() }
    }

    private fun setResultByAction(actionType: String) {
        setFragmentResult(
            REQUEST_KEY_IMAGE_SOURCE, bundleOf(
                BundleKeys.EVENT to actionType
            )
        )
        dismiss()
    }

    companion object {
        const val TAG = "AttachFileBottomSheet"
        const val REQUEST_KEY_IMAGE_SOURCE = "REQUEST_KEY_IMAGE_SOURCE"
        const val ACTION_CAMERA = "ACTION_CAMERA"
        const val ACTION_GALLERY = "ACTION_GALLERY"

        fun newInstance(): ImageSourceChooserBottomSheet {
            return ImageSourceChooserBottomSheet()
        }
    }
}