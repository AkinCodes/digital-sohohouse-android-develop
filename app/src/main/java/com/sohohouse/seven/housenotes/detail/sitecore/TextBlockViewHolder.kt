package com.sohohouse.seven.housenotes.detail.sitecore

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.setLinkableHtml
import com.sohohouse.seven.databinding.ItemHouseNoteDetailBodyTextblockBinding

class TextBlockViewHolder(private val binding: ItemHouseNoteDetailBodyTextblockBinding) :
    RecyclerView.ViewHolder(
        binding.root
    ) {

    fun bind(houseNoteDetailTextBlockItem: HouseNoteDetailTextBlockItem) {
        binding.bodyTextBlock.setLinkableHtml(houseNoteDetailTextBlockItem.paragraph)
    }
}