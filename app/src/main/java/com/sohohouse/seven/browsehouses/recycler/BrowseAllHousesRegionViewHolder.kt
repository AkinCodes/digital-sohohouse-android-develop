package com.sohohouse.seven.browsehouses.recycler

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.databinding.BrowseHousesRegionItemBinding

class BrowseAllHousesRegionViewHolder(private val binding: BrowseHousesRegionItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: BaseAdapterItem.BrowseHousesItem.RegionHeader) {
        binding.regionName.text = getString(item.titleRes)
    }
}