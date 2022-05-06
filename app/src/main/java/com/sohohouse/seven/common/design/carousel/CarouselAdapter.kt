package com.sohohouse.seven.common.design.carousel

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.ComponentCarouselItemBinding

class CarouselAdapter<T : CarouselItem>(
    private val onItemClicked: (item: T, imageView: ImageView, position: Int) -> Unit
) : RecyclerView.Adapter<CarouselItemViewHolder<T>>() {

    var items: List<T> = mutableListOf()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselItemViewHolder<T> {
        return CarouselItemViewHolder(
            ComponentCarouselItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CarouselItemViewHolder<T>, position: Int) {
        holder.bind(items[position], onItemClicked)
    }
}