package com.sohohouse.seven.connect.mynetwork.blockedprofiles

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.ViewHolderBlockedProfileBinding

class BlockedProfileRenderer(
    private val onUnblockClick: (BlockedProfile) -> Unit
) : Renderer<BlockedProfile, BlockedProfileViewHolder> {

    override val type: Class<BlockedProfile>
        get() = BlockedProfile::class.java

    override fun createViewHolder(parent: ViewGroup): BlockedProfileViewHolder {
        return BlockedProfileViewHolder(
            ViewHolderBlockedProfileBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun bindViewHolder(holder: BlockedProfileViewHolder, item: BlockedProfile) {
        holder.bind(item, onUnblockClick)
    }

}