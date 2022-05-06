package com.sohohouse.seven.housenotes.detail.sitecore

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.databinding.ItemHouseNoteDetailsGridImageBinding

class GridImageBlockViewHolder(private val binding: ItemHouseNoteDetailsGridImageBinding) :
    RecyclerView.ViewHolder(
        binding.root
    ) {

    val set = ConstraintSet()

    fun bind(houseNoteDetailGridImageBlockItem: HouseNoteDetailGridImageBlockItem) {
        with(binding.houseNoteInlineImage) {
            houseNoteImageCaptionItem.text = houseNoteDetailGridImageBlockItem.caption

            val ratio = "H,${houseNoteDetailGridImageBlockItem.aspectRatio}:1"
            set.clone(constraintLayout)
            set.setDimensionRatio(houseNoteImageItem.id, ratio)
            set.applyTo(constraintLayout)

            houseNoteImageItem.post {
                houseNoteImageItem.setImageFromUrl(houseNoteDetailGridImageBlockItem.url)
            }
        }
    }

    private val constraintLayout: ConstraintLayout get() = binding.houseNoteInlineImage.imageContainer
}