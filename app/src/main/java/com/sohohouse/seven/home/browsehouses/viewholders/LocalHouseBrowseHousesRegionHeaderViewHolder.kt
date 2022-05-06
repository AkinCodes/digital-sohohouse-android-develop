package com.sohohouse.seven.home.browsehouses.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.databinding.LocalHouseBrowseHousesRegionHeaderLayoutBinding

const val LOCAL_HOUSE_BROWSE_HOUSES_REGION_HEADER_LAYOUT =
    R.layout.local_house_browse_houses_region_header_layout

class LocalHouseBrowseHousesRegionHeaderViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    private val binding = LocalHouseBrowseHousesRegionHeaderLayoutBinding.bind(itemView)

    fun bind(item: BaseAdapterItem.BrowseHousesItem.RegionHeader) {
        binding.headerTitle.text = getString(item.titleRes)
    }
}