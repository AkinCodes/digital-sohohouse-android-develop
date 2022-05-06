package com.sohohouse.seven.connect.mynetwork.connections

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.common.views.preferences.ProfileItem

class ProfileItemRenderer(
    private val onClick: (ProfileItem) -> Unit,
    private val onMessageClick: (ProfileItem) -> Unit
) : Renderer<ProfileItem, ProfileItemViewHolder> {

    override val type: Class<ProfileItem>
        get() = ProfileItem::class.java

    override fun createViewHolder(parent: ViewGroup): ProfileItemViewHolder {
        return ProfileItemViewHolder(createItemView(parent, R.layout.view_holder_profile_item))
    }

    override fun bindViewHolder(holder: ProfileItemViewHolder, item: ProfileItem) {
        holder.bind(item, onClick, onMessageClick)
    }
}

