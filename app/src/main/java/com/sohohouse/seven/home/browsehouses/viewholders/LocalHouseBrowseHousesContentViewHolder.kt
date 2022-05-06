package com.sohohouse.seven.home.browsehouses.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.LocalHouseBrowseHousesContentLayoutBinding

const val LOCAL_HOUSE_BROWSE_HOUSES_CONTENT_LAYOUT =
    R.layout.local_house_browse_houses_content_layout

class LocalHouseBrowseHousesContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val binding = LocalHouseBrowseHousesContentLayoutBinding.bind(itemView)

    fun bind(item: BaseAdapterItem.BrowseHousesItem.Content) = with(binding) {
        val house = item.house

        browseHouseTitle.text = house.name

        val imageUrl = house.house.get(house.document)?.houseImageSet?.largePng
        browseHouseImage.clipToOutline = true
        browseHouseImage.setImageFromUrl(imageUrl)

        browseHouseIcon.setImageFromUrl(house.venueIcons.darkPng, R.drawable.ic_soho_house)

        lastItemSpacer.setVisible(item.isLastItem)
    }

    fun setHouseClickListener(onNext: (Any) -> Unit) {
        binding.root.clicks(onNext)
    }

}