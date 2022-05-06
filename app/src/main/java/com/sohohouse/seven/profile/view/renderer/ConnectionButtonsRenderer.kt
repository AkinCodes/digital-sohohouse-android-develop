package com.sohohouse.seven.profile.view.renderer

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.ViewHolderConnectionButtonsBinding
import com.sohohouse.seven.profile.view.model.Button
import com.sohohouse.seven.profile.view.model.Buttons
import com.sohohouse.seven.profile.view.viewholder.ConnectionButtonsViewHolder

class ConnectionButtonsRenderer(
    private val onClickButton: (Button) -> Unit
) : Renderer<Buttons, ConnectionButtonsViewHolder> {

    override val type: Class<Buttons>
        get() = Buttons::class.java

    override fun createViewHolder(parent: ViewGroup): ConnectionButtonsViewHolder {
        return ConnectionButtonsViewHolder(
            ViewHolderConnectionButtonsBinding.bind(
                createItemView(parent, R.layout.view_holder_connection_buttons)
            ),
            onClickButton
        )
    }

    override fun bindViewHolder(holder: ConnectionButtonsViewHolder, item: Buttons) {
        holder.bind(item)
    }
}