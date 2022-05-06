package com.sohohouse.seven.housenotes.detail.sitecore

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.extensions.toggleVisibiltyIfEmpty
import com.sohohouse.seven.databinding.HouseNoteDetailHeaderCardLayoutBinding

class HeaderViewHolder(private val binding: HouseNoteDetailHeaderCardLayoutBinding) :
    RecyclerView.ViewHolder(
        binding.root
    ) {

    fun bind(item: HouseNoteDetailHeaderItem) = with(binding) {
        headerHouseName.setGone()
        headerTitle.text = item.title
        headerLine.text = item.subtitle
        headerAuthor.text = item.author
        headerDate.text = item.articleDate
        headerLine.toggleVisibiltyIfEmpty()
    }

}