package com.sohohouse.seven.common.design.carousel

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.ItemHomeContentSectionHeaderBinding

class CarouselHeaderRenderer<T : CarouselHeader>(
    override val type: Class<T>,
    private val onSeeAllClick: () -> Unit = {}
) : Renderer<T, CarouselHeaderViewHolder> {

    override fun createViewHolder(parent: ViewGroup): CarouselHeaderViewHolder {
        val binding = ItemHomeContentSectionHeaderBinding.bind(createItemView(parent, R.layout.compnent_section_header))
        binding.seeAll.setOnClickListener { onSeeAllClick() }
        return CarouselHeaderViewHolder(binding)
    }

    override fun bindViewHolder(holder: CarouselHeaderViewHolder, item: T) {
        holder.bind(item)
    }

}