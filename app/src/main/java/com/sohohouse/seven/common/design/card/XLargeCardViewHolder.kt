package com.sohohouse.seven.common.design.card

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.databinding.LocalHouseHouseNoteContentLayoutBinding
import com.sohohouse.seven.network.sitecore.SitecoreResourceFactory

class XLargeCardViewHolder<T : XLargeCard>(private val binding: LocalHouseHouseNoteContentLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: T, onItemClicked: (item: T, position: Int) -> Unit) = with(binding) {
        root.setOnClickListener { onItemClicked(item, adapterPosition) }
        houseNoteTitle.text = item.title
        houseNoteImage.setImageFromUrl(
            SitecoreResourceFactory.getImageUrl(
                item.imageUrl ?: ""
            )
        )
    }
}