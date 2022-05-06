package com.sohohouse.seven.housenotes.detail.sitecore

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.views.carousel.CarouselGalleryAdapter
import com.sohohouse.seven.databinding.CarouselContainerLayoutBinding


class ImageCarouselBlockViewHolder(private val binding: CarouselContainerLayoutBinding) :
    RecyclerView.ViewHolder(
        binding.root
    ) {

    fun bind(houseNoteDetailImageCarouselBlockItem: HouseNoteDetailImageCarouselBlockItem) {
        binding.houseNoteGallery.apply {
            adapter = CarouselGalleryAdapter(houseNoteDetailImageCarouselBlockItem.images)
            layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
        }
    }

}