package com.sohohouse.seven.common.views.dialog

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseBottomSheet
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setFragmentResult
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.views.dialog.CustomDialogBuilder.Companion.REQ_KEY_NEGATIVE_BTN_CLICK
import com.sohohouse.seven.common.views.dialog.CustomDialogBuilder.Companion.REQ_KEY_POSITIVE_BTN_CLICK
import com.sohohouse.seven.databinding.BottomSheetDialogBinding

class CustomBottomSheetDialog : BaseBottomSheet() {

    class Builder : CustomDialogBuilder<CustomBottomSheetDialog>() {
        override fun newInstance(): CustomBottomSheetDialog {
            return CustomBottomSheetDialog()
        }
    }

    private val titleText: String? get() = arguments?.getString(BundleKeys.TITLE)
    private val messageText: String? get() = arguments?.getString(BundleKeys.MESSAGE)
    private val positiveBtnText: String? get() = arguments?.getString(BundleKeys.POSITIVE_BTN_TEXT)
    private val negativeBtnText: String? get() = arguments?.getString(BundleKeys.NEGATIVE_BTN_TEXT)

    override val contentLayout: Int
        get() = R.layout.bottom_sheet_dialog

    override val isDraggable: Boolean
        get() = false

    override val fixedHeight: Int?
        get() = ViewGroup.LayoutParams.WRAP_CONTENT

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(BottomSheetDialogBinding.bind(view)) {
            if (titleText != null) {
                title.text = titleText
            } else {
                title.setGone()
            }
            if (messageText != null) {
                message.text = messageText
            } else {
                message.setGone()
            }
            if (positiveBtnText != null) {
                positiveBtn.text = positiveBtnText
            } else {
                positiveBtn.setGone()
            }
            if (negativeBtnText != null) {
                negativeBtn.text = negativeBtnText
            } else {
                negativeBtn.setGone()
            }

            positiveBtn.clicks {
                setFragmentResult(REQ_KEY_POSITIVE_BTN_CLICK)
                dismiss()
            }
            negativeBtn.clicks {
                setFragmentResult(REQ_KEY_NEGATIVE_BTN_CLICK)
                dismiss()
            }
        }
    }

}