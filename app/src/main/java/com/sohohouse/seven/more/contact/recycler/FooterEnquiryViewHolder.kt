package com.sohohouse.seven.more.contact.recycler

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.databinding.MoreContactBottomSectionBinding

class FooterEnquiryViewHolder(private val binding: MoreContactBottomSectionBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun clicks(onClickListener: (Any) -> Unit) {
        binding.clickableItem.clicks(onClickListener)
    }
}