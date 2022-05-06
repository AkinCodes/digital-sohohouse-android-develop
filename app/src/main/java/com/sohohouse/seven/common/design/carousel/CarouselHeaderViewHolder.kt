package com.sohohouse.seven.common.design.carousel

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.ItemHomeContentSectionHeaderBinding

class CarouselHeaderViewHolder(private val binding: ItemHomeContentSectionHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: CarouselHeader) = with(binding) {
        title.setText(item.title)
        subtitle.setText(item.subtitle)
        seeAll.setVisible(item.hasMore)
    }

}