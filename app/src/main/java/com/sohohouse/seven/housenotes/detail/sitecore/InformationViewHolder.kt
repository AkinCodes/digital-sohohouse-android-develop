package com.sohohouse.seven.housenotes.detail.sitecore

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.ItemHouseNoteInformationBinding

class InformationViewHolder(private val binding: ItemHouseNoteInformationBinding) :
    RecyclerView.ViewHolder(
        binding.root
    ) {

    fun bind(houseNoteDetailInformationItem: HouseNoteDetailInformationItem) {
        binding.footerNote.text = houseNoteDetailInformationItem.text
    }

}