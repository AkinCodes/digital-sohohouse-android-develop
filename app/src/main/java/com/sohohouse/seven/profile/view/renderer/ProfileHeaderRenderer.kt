package com.sohohouse.seven.profile.view.renderer

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.databinding.ViewHolderProfileHeaderBinding
import com.sohohouse.seven.profile.view.viewholder.ProfileHeaderViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class ProfileHeaderRenderer(
    private val coroutineScope: Lazy<CoroutineScope>,
    private val onProfileAvatarClick: () -> Unit
) : Renderer<ProfileHeaderRenderer.ProfileHeaderItem, ProfileHeaderViewHolder> {

    override val type: Class<ProfileHeaderItem>
        get() = ProfileHeaderItem::class.java

    override fun createViewHolder(parent: ViewGroup): ProfileHeaderViewHolder {
        return ProfileHeaderViewHolder(
            ViewHolderProfileHeaderBinding.bind(
                createItemView(parent, R.layout.view_holder_profile_header)
            ),
            coroutineScope,
            onProfileAvatarClick
        )
    }

    override fun bindViewHolder(holder: ProfileHeaderViewHolder, item: ProfileHeaderItem) {
        holder.bind(item)
    }

    data class ProfileHeaderItem(
        val profileItem: ProfileItem,
        val userAvailableStatusColorAttrRes: Flow<Pair<Boolean, Int>>
    ) : DiffItem {
        override val key: Any
            get() = profileItem.key
    }

}