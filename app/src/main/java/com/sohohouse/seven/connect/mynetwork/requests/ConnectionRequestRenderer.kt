package com.sohohouse.seven.connect.mynetwork.requests

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.design.adapter.Renderer

class ConnectionRequestRenderer(
    private val onClickProfile: (ConnectionRequestItem) -> Unit = {},
    private val onClickAccept: (ConnectionRequestItem) -> Unit = {},
    private val onClickIgnore: (ConnectionRequestItem) -> Unit = {}
) : Renderer<ConnectionRequestItem, ConnectionRequestViewHolder> {

    override val type: Class<ConnectionRequestItem>
        get() = ConnectionRequestItem::class.java

    override fun createViewHolder(parent: ViewGroup): ConnectionRequestViewHolder {
        return ConnectionRequestViewHolder(
            createItemView(
                parent,
                R.layout.view_holder_connection_request
            )
        )
    }

    override fun bindViewHolder(holder: ConnectionRequestViewHolder, item: ConnectionRequestItem) {
        holder.bind(item, onClickProfile, onClickAccept, onClickIgnore)
    }
}