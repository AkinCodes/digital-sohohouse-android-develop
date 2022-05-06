package com.sohohouse.seven.profile.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.ViewHolderProfileMessageBinding
import com.sohohouse.seven.profile.view.model.MessageItem

class MessageItemViewHolder(private val binding: ViewHolderProfileMessageBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: MessageItem) {
        binding.message.text = item.message
    }
}