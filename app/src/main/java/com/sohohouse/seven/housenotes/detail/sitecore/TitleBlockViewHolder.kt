package com.sohohouse.seven.housenotes.detail.sitecore

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.ItemHouseNoteDetailBodyTitleblockBinding


class TitleBlockViewHolder(private val binding: ItemHouseNoteDetailBodyTitleblockBinding) :
    RecyclerView.ViewHolder(
        binding.root
    ) {

    fun bind(houseNoteDetailTitleBlockItem: HouseNoteDetailTitleBlockItem) {
        binding.bodyTitle.text = houseNoteDetailTitleBlockItem.title
    }

}