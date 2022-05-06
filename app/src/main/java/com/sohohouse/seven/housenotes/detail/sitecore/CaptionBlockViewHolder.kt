package com.sohohouse.seven.housenotes.detail.sitecore

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.ItemHouseNoteDetailsImageCaptionBlockBinding

class CaptionBlockViewHolder(private val binding: ItemHouseNoteDetailsImageCaptionBlockBinding) :
    RecyclerView.ViewHolder(
        binding.root
    ) {

    fun bind(houseNoteDetailImageCaptionBlockItem: HouseNoteDetailImageCaptionBlockItem) {
        binding.bodyImageCaption.text = houseNoteDetailImageCaptionBlockItem.caption
    }

}