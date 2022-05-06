package com.sohohouse.seven.common.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.book.adapter.model.ZeroStateAdapterItem
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.databinding.FilterZeroStateLayoutBinding

const val ZERO_STATE_INFO_LAYOUT = R.layout.filter_zero_state_layout

class FilterZeroStateViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = FilterZeroStateLayoutBinding.bind(view)

    private fun setTitle(stringRes: Int) {
        binding.title.setText(stringRes)
    }

    private fun setSupporting(stringRes: Int) {
        binding.supporting.setText(stringRes)
    }

    fun bind(adapterItem: ZeroStateAdapterItem) {
        setTitle(adapterItem.titleStringRes)
        setSupporting(adapterItem.supportingStringRes)
    }

    fun bind(adapterItem: BaseAdapterItem.HouseNoteItem.ResultsEmpty) {
        setTitle(adapterItem.titleStringRes)
        setSupporting(adapterItem.supportingStringRes)
    }
}