package com.sohohouse.seven.common.views.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setFragmentResult
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.databinding.FragmentCustomDialogBinding

class CustomModalDialog : AppCompatDialogFragment() {

    class Builder : CustomDialogBuilder<CustomModalDialog>() {
        override fun newInstance(): CustomModalDialog {
            return CustomModalDialog()
        }
    }

    private val title: String? get() = arguments?.getString(BundleKeys.TITLE)
    private val message: String? get() = arguments?.getString(BundleKeys.MESSAGE)
    private val positiveBtnText: String? get() = arguments?.getString(BundleKeys.POSITIVE_BTN_TEXT)
    private val negativeBtnText: String? get() = arguments?.getString(BundleKeys.NEGATIVE_BTN_TEXT)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentCustomDialogBinding.inflate(
            layoutInflater,
            container,
            false
        ).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentCustomDialogBinding.bind(view)

        if (title != null) {
            binding.customModalTitle.text = title
        } else {
            binding.customModalTitle.setGone()
        }
        if (message != null) {
            binding.customModalMsg.text = message
        } else {
            binding.customModalMsg.setGone()
        }
        if (positiveBtnText != null) {
            binding.customModalPositive.text = positiveBtnText
        } else {
            binding.customModalPositive.setGone()
        }
        if (negativeBtnText.isNullOrEmpty().not()) {
            binding.customModalNegative.text = negativeBtnText
        } else {
            binding.customModalNegative.setGone()
        }

        binding.customModalPositive.clicks {
            setFragmentResult(CustomDialogBuilder.REQ_KEY_POSITIVE_BTN_CLICK)
            dismiss()
        }
        binding.customModalNegative.clicks {
            setFragmentResult(CustomDialogBuilder.REQ_KEY_NEGATIVE_BTN_CLICK)
            dismiss()
        }
    }
}