package com.sohohouse.seven.common.design.carousel

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.ComponentCarouselItemBinding

class CarouselItemViewHolder<T : CarouselItem>(private val binding: ComponentCarouselItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: T, onItemClicked: (item: T, imageView: ImageView, position: Int) -> Unit) =
        with(binding) {
            title.text = item.title
            subtitle.text = item.subtitle
            caption.setText(item.caption)
            image.setImageUrl(item.imageUrl)
            root.setOnClickListener { onItemClicked(item, image, adapterPosition) }
        }
}