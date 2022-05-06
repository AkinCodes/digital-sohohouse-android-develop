package com.sohohouse.seven.connect.shareprofile

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sohohouse.seven.common.extensions.context
import com.sohohouse.seven.databinding.SharePofileItemBinding

class ShareProfileViewHolder(
    private val binding: SharePofileItemBinding,
    private val onClick: () -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(profileImageURL: String) {
        binding.root.setOnClickListener { onClick() }

        Glide.with(context)
            .load(profileImageURL)
            .circleCrop()
            .into(binding.profileImage)
    }

}