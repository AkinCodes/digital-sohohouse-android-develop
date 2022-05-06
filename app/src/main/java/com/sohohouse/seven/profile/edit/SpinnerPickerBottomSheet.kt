package com.sohohouse.seven.profile.edit

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseBottomSheet
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setUp
import com.sohohouse.seven.common.extensions.withResultListener
import com.sohohouse.seven.databinding.BottomSheetPickIndustryBinding

class SpinnerPickerBottomSheet : BaseBottomSheet() {

    companion object {
        const val REQ_KEY_PICK_ITEM = "REQ_KEY_PICK_ITEM"
    }

    class Builder(
        private var listener: ((selectedItem: String?) -> Unit)? = null,
        private var header: String? = null,
        private var options: ArrayList<String>? = null,
        private var initialValue: String? = null,
        private var placeholder: String? = null
    ) {
        fun build() = SpinnerPickerBottomSheet().also {
            it.arguments = Bundle().apply {
                putString(BundleKeys.PAGE_HEADER, this@Builder.header)
                if (options != null) {
                    putStringArrayList(BundleKeys.SPINNER_OPTIONS, options)
                } else throw IllegalStateException("Must pass list of options")
                putString(BundleKeys.INITIAL_VALUE, initialValue)
                putString(BundleKeys.PLACEHOLDER, placeholder)
            }
            it.withResultListener(REQ_KEY_PICK_ITEM) { _, bundle ->
                this@Builder.listener?.invoke(
                    bundle.getString(
                        BundleKeys.SPINNER_SELECTED_ITEM,
                        null
                    )
                )
            }
        }
    }

    override val fixedHeight: Int?
        get() = ViewGroup.LayoutParams.WRAP_CONTENT

    private var selected: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = BottomSheetPickIndustryBinding.bind(view)

        binding.header.editProfileModalFieldTitle.text =
            arguments?.getString(BundleKeys.PAGE_HEADER)
        selected = arguments?.getString(BundleKeys.INITIAL_VALUE)
        val placeholder = arguments?.getString(BundleKeys.PLACEHOLDER)
        val options =
            arguments?.getStringArrayList(BundleKeys.SPINNER_OPTIONS)!!.toMutableList().apply {
                if (selected == null && placeholder != null) add(0, placeholder)
            }
        binding.picker.setUp(
            options.map { it.toPickerItem() },
            selected.toPickerItem(),
            ::onValueChange,
            placeholder
        )
        binding.header.editProfileModalDone.clicks { onConfirmed(selected) }
    }

    private fun onValueChange(pickerItem: PickerItem?) {
        this.selected = pickerItem?.value
    }

    private fun onConfirmed(value: String?) {
        setFragmentResult(REQ_KEY_PICK_ITEM, bundleOf(BundleKeys.SPINNER_SELECTED_ITEM to value))
        dismiss()
    }

    private fun String?.toPickerItem() = object : PickerItem {
        override val value: String
            get() = this@toPickerItem ?: ""
    }

    override val contentLayout: Int
        get() = R.layout.bottom_sheet_pick_industry
}