package com.sohohouse.seven.book.eventdetails

import android.app.ActionBar
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseBottomSheet
import com.sohohouse.seven.databinding.ConfirmBookingBottomSheetBinding

class ConfirmBookingBottomSheet : BaseBottomSheet() {

    override val fixedHeight: Int
        get() = ViewGroup.LayoutParams.WRAP_CONTENT

    companion object {
        const val TAG = "ConfirmBookingBottomSheet"
        const val ON_CONFIRM = "ON_CONFIRM"
    }

    private val binding by viewBinding(ConfirmBookingBottomSheetBinding::bind)

    override val contentLayout: Int = R.layout.confirm_booking_bottom_sheet

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.confirm.setOnClickListener {
            dismiss()
            requireActivity().supportFragmentManager.setFragmentResult(ON_CONFIRM, bundleOf())
        }
    }

}