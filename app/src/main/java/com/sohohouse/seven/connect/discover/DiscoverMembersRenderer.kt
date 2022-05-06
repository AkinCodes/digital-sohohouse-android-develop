package com.sohohouse.seven.connect.discover

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.FindMembersItemBinding

class DiscoverMembersRenderer(
    private val onClick: (DiscoverMembersAdapterItem) -> Unit,
    private val profileImageURL: String
) : Renderer<GetDiscoverMembersAdapterItem, DiscoverMembersViewHolder> {

    override val type: Class<GetDiscoverMembersAdapterItem>
        get() = GetDiscoverMembersAdapterItem::class.java

    override fun createViewHolder(parent: ViewGroup): DiscoverMembersViewHolder {
        return DiscoverMembersViewHolder(
            FindMembersItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), onClick
        )
    }

    override fun bindViewHolder(
        holder: DiscoverMembersViewHolder,
        item: GetDiscoverMembersAdapterItem
    ) {
        holder.bind(item, profileImageURL)
    }

}