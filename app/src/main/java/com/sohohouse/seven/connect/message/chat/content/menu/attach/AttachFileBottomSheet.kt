package com.sohohouse.seven.connect.message.chat.content.menu.attach

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseBottomSheet
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.databinding.FragmentAttachFileMenuBinding

class AttachFileBottomSheet : BaseBottomSheet() {
    override val contentLayout: Int
        get() = R.layout.fragment_attach_file_menu

    override val fixedHeight: Int = ViewGroup.LayoutParams.WRAP_CONTENT

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FragmentAttachFileMenuBinding.bind(view).initView()
    }

    private fun FragmentAttachFileMenuBinding.initView() {
        cameraImageBtn.setOnClickListener { setResultByAction(ATTACH_TYPE_CAMERA_IMAGE) }
        cameraVideoBtn.setOnClickListener { setResultByAction(ATTACH_TYPE_CAMERA_VIDEO) }
        mediaLibraryBtn.setOnClickListener { setResultByAction(ATTACH_TYPE_MEDIA) }
//        documentBtn.setOnClickListener { setResultByAction(ATTACH_TYPE_DOC) }
        cancelBtn.setOnClickListener { dismiss() }
    }

    private fun setResultByAction(actionType: String) {
        setFragmentResult(
            REQUEST_KEY_ATTACH_FILE, bundleOf(
                BundleKeys.EVENT to actionType
            )
        )
        dismiss()
    }

    companion object {
        const val TAG = "AttachFileBottomSheet"
        const val REQUEST_KEY_ATTACH_FILE = "REQUEST_KEY_ATTACH_FILE"
        const val ATTACH_TYPE_CAMERA_IMAGE = "ATTACH_TYPE_CAMERA_IMAGE"
        const val ATTACH_TYPE_CAMERA_VIDEO = "ATTACH_TYPE_CAMERA_VIDEO"
        const val ATTACH_TYPE_MEDIA = "ATTACH_TYPE_MEDIA"
        const val ATTACH_TYPE_DOC = "ATTACH_TYPE_DOC"

        fun newInstance(): AttachFileBottomSheet {
            return AttachFileBottomSheet()
        }
    }
}