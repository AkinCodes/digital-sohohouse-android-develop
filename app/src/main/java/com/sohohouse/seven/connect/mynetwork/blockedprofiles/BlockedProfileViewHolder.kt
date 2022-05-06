package com.sohohouse.seven.connect.mynetwork.blockedprofiles

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.setTextOrHide
import com.sohohouse.seven.databinding.ViewHolderBlockedProfileBinding

class BlockedProfileViewHolder(val binding: ViewHolderBlockedProfileBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: BlockedProfile,
        onUnblock: (BlockedProfile) -> Unit,
    ) {
        with(binding) {
            profileImage.setImageUrl(item.imageUrl, R.drawable.ic_account_on)
            title.text = item.fullName
            subtitle.setTextOrHide(item.occupation)
            buttonUnblock.setOnClickListener { onUnblock(item) }
        }
    }

}