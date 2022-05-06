package com.sohohouse.seven.connect.discover

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sohohouse.seven.common.extensions.context
import com.sohohouse.seven.databinding.FindMembersItemBinding

class DiscoverMembersViewHolder(
    private val binding: FindMembersItemBinding,
    private val onClick: (DiscoverMembersAdapterItem) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: GetDiscoverMembersAdapterItem, profileImageURL: String) {
        binding.root.setOnClickListener { onClick(item()) }

        Glide.with(context)
            .load(profileImageURL)
            .circleCrop()
            .into(binding.profileImage)
    }

}