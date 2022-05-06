package com.sohohouse.seven.more.contact.recycler

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.AdapterView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.databinding.ViewSpinnerEnquiryBinding
import com.sohohouse.seven.more.contact.EnquirySpinnerArrayAdapter
import com.sohohouse.seven.more.contact.EnquiryType

interface EnquirySpinnerSelectedListener {
    fun spinnerSelected(
        parentData: SpinnerEnquiryItemType,
        childEnquiryType: EnquiryType,
        selectedIndex: Int
    )
}

class SpinnerEnquiryViewHolder(private val binding: ViewSpinnerEnquiryBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(data: SpinnerEnquiryItemType, listener: EnquirySpinnerSelectedListener) =
        with(binding) {
            val enquiryType = data.enquiryType
            val enquiryTypes = enquiryType.childEnqTypes
            if (enquiryTypes != null) {
                val spinnerArrayAdapter = EnquirySpinnerArrayAdapter(
                    itemView.context,
                    R.layout.item_inquiry_type_spinner,
                    enquiryTypes
                )
                spinnerArrayAdapter.setDropDownViewResource(R.layout.list_item_inquiry_type_spinner)

                header.text = getString(enquiryType.headerTextRes)
                spinner.adapter = spinnerArrayAdapter
                var selectedIndex = data.selectedIndex
                if (selectedIndex != null) {
                    spinner.setSelection(selectedIndex)
                } else {
                    spinner.setSelection(enquiryTypes.lastIndex + 1)
                }

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position != enquiryTypes.lastIndex + 1 && position != selectedIndex) {
                            selectedIndex = position
                            spinner.shouldScrollToTop = false
                            listener.spinnerSelected(data, enquiryTypes[position], position)
                        }
                    }
                }
            }
        }

    fun setIsEnabled(isEnabled: Boolean) {
        binding.spinner.isEnabled = isEnabled
    }
}