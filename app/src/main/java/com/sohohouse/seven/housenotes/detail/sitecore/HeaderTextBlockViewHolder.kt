package com.sohohouse.seven.housenotes.detail.sitecore

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.ItemHouseNoteDetailsHeaderTextblockBinding
import com.sohohouse.seven.housenotes.detail.sitecore.HouseNoteDetailHeaderTextBlockItem.HeadingStyle.*


class HeaderTextBlockViewHolder(private val binding: ItemHouseNoteDetailsHeaderTextblockBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: HouseNoteDetailHeaderTextBlockItem) {
        with(binding.headerTextview) {
            text = item.text
            gravity = item.alignment
            setTextAppearance(
                when (item.headingStyle) {
                    h1 -> R.style.house_note_h1
                    h2 -> R.style.house_note_h2
                    h3 -> R.style.house_note_h3
                }
            )
        }
    }

}