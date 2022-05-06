package com.sohohouse.seven.housenotes.detail.sitecore

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.setLinkableHtml
import com.sohohouse.seven.databinding.ItemHouseNoteDetailCreditBinding

class CreditViewHolder(private val binding: ItemHouseNoteDetailCreditBinding) :
    RecyclerView.ViewHolder(
        binding.root
    ) {

    fun bind(houseNoteDetailCreditItem: HouseNoteDetailCreditItem) {
        binding.footerCredit.setLinkableHtml(houseNoteDetailCreditItem.credits)
    }

}