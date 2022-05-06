package com.sohohouse.seven.profile.view.renderer

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.ViewHolderListItemBinding
import com.sohohouse.seven.profile.view.model.ProfileAction
import com.sohohouse.seven.profile.view.viewholder.ConnectActionViewHolder

class ConnectActionRenderer(
    private val onClick: (ProfileAction) -> Unit
) : Renderer<ProfileAction, ConnectActionViewHolder> {

    override val type: Class<ProfileAction>
        get() = ProfileAction::class.java

    override fun createViewHolder(parent: ViewGroup): ConnectActionViewHolder {
        return ConnectActionViewHolder(
            ViewHolderListItemBinding.bind(
                createItemView(parent, R.layout.view_holder_list_item)
            )
        )
    }

    override fun bindViewHolder(holder: ConnectActionViewHolder, item: ProfileAction) {
        holder.bind(item, onClick)
    }
}