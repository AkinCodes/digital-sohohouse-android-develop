package com.sohohouse.seven.connect.match

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseBottomSheet
import com.sohohouse.seven.databinding.OptinCompleteBottomSheetBinding

class OptInCompleteBottomSheet : BaseBottomSheet() {

    override val contentLayout: Int = R.layout.optin_complete_bottom_sheet

    override val fixedHeight = ViewGroup.LayoutParams.WRAP_CONTENT

    val binding by viewBinding(OptinCompleteBottomSheetBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.optInCompletedDoneBtn.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "OptInCompleteBottomSheet"

        fun newInstance(): OptInCompleteBottomSheet {
            return OptInCompleteBottomSheet()
        }
    }
}