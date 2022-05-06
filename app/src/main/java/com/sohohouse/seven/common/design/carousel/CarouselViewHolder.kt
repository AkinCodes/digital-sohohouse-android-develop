package com.sohohouse.seven.common.design.carousel

import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.context
import com.sohohouse.seven.databinding.ComponentCarouselBinding

class CarouselViewHolder<T : CarouselItem>(
    binding: ComponentCarouselBinding,
    onItemClicked: (item: T, imageView: ImageView, position: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val adapter = CarouselAdapter(onItemClicked)

    init {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = adapter
    }

    fun bind(item: Carousel<T>) {
        adapter.items = item.items
    }
}