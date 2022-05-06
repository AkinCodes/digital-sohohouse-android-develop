package com.sohohouse.seven.common.design.carousel

import android.view.ViewGroup
import android.widget.ImageView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.ComponentCarouselBinding

open class CarouselRenderer<C : Carousel<T>, T : CarouselItem>(
    override val type: Class<C>,
    private val onItemClicked: (item: T, imageView: ImageView, position: Int) -> Unit
) : Renderer<C, CarouselViewHolder<T>> {

    override fun createViewHolder(parent: ViewGroup): CarouselViewHolder<T> {
        return CarouselViewHolder(
            ComponentCarouselBinding.bind(createItemView(parent, R.layout.component_carousel)),
            onItemClicked
        )
    }

    override fun bindViewHolder(holder: CarouselViewHolder<T>, item: C) {
        holder.bind(item)
    }

}