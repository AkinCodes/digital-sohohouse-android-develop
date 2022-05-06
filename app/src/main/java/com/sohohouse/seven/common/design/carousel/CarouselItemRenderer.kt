package com.sohohouse.seven.common.design.carousel

import android.view.ViewGroup
import android.widget.ImageView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.ComponentCarouselItemBinding

class CarouselItemRenderer<T : CarouselItem>(
    override val type: Class<T>,
    private val onItemClicked: (item: T, imageView: ImageView?, position: Int) -> Unit
) : Renderer<T, CarouselItemViewHolder<T>> {

    override fun createViewHolder(parent: ViewGroup): CarouselItemViewHolder<T> {
        return CarouselItemViewHolder(
            ComponentCarouselItemBinding
                .bind(createItemView(parent, R.layout.component_carousel_item))
        )
    }

    override fun bindViewHolder(holder: CarouselItemViewHolder<T>, item: T) {
        holder.bind(item, onItemClicked)
    }
}