package com.sohohouse.seven.profile.view.renderer

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.ViewHolderProfileMessageBinding
import com.sohohouse.seven.profile.view.model.MessageItem
import com.sohohouse.seven.profile.view.viewholder.MessageItemViewHolder

class MessageRenderer : Renderer<MessageItem, MessageItemViewHolder> {

    override val type: Class<MessageItem>
        get() = MessageItem::class.java

    override fun createViewHolder(parent: ViewGroup): MessageItemViewHolder {
        return MessageItemViewHolder(
            ViewHolderProfileMessageBinding.bind(
                createItemView(parent, R.layout.view_holder_profile_message)
            )
        )
    }

    override fun bindViewHolder(holder: MessageItemViewHolder, item: MessageItem) {
        holder.bind(item)
    }
}