package com.sohohouse.seven.housenotes.detail.sitecore

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.ItemHouseNoteDetailQuoteblockBinding


class QuoteBlockViewHolder(private val binding: ItemHouseNoteDetailQuoteblockBinding) :
    RecyclerView.ViewHolder(
        binding.root
    ) {

    fun bind(houseNoteDetailQuoteBlockItem: HouseNoteDetailQuoteBlockItem) {
        binding.bodyPullquote.text = houseNoteDetailQuoteBlockItem.quote
    }

}