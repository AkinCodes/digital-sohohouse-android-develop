package com.sohohouse.seven.common.views.carousel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseRecyclerDiffAdapter
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.databinding.CarouselGalleryItemBinding
import com.sohohouse.seven.housenotes.detail.sitecore.HouseNoteDetailImageCarouselBlockItem

class CarouselGalleryAdapter(photos: List<HouseNoteDetailImageCarouselBlockItem.Image>) :
    BaseRecyclerDiffAdapter<CarouselGalleryAdapter.GalleryViewHolder, HouseNoteDetailImageCarouselBlockItem.Image>() {

    init {
        submitList(photos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): GalleryViewHolder {
        return GalleryViewHolder(
            CarouselGalleryItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    class GalleryViewHolder(private val binding: CarouselGalleryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: HouseNoteDetailImageCarouselBlockItem.Image) = with(binding) {
            galleryImage.setImageUrl(photo.imageLarge, R.drawable.placeholder)
            galleryCaption.setVisible(photo.imageCaption.isNotEmpty() || photo.imageCredit.isNotEmpty())
            galleryCaption.text =
                if (photo.imageCaption.isEmpty() || photo.imageCredit.isEmpty())
                    listOf(photo.imageCaption, photo.imageCredit).concatenateWithSpace()
                else
                    getString(R.string.more_content_caption_credits_label).replaceBraces(
                        photo.imageCaption,
                        photo.imageCredit
                    )

            galleryTag.text = photo.tag
            galleryTag.setVisible(!photo.tag.isNullOrEmpty())
        }
    }

    override fun onBindViewHolder(viewHolder: GalleryViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

}
