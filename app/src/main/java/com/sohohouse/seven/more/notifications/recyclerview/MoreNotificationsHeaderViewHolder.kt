package com.sohohouse.seven.more.notifications.recyclerview

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.MoreNotificationsHeaderBinding

class MoreNotificationsHeaderViewHolder(private val binding: MoreNotificationsHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: MoreNotificationsHeaderAdapterItem) {
        binding.headerText.text = item.text
    }
}