package com.sohohouse.seven.common.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.book.adapter.model.FilterStateHeaderAdapterItem
import com.sohohouse.seven.databinding.FilterStateHeaderBinding

class FilterStateViewHolder(private val binding: FilterStateHeaderBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(adapterItem: FilterStateHeaderAdapterItem) {
        if (adapterItem.isFiltered) {
            binding.label.setText(adapterItem.titleRes)
        }
    }
}