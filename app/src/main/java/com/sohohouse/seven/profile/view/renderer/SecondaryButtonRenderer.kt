package com.sohohouse.seven.profile.view.renderer

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.ViewHolderSecondaryButtonBinding
import com.sohohouse.seven.profile.view.model.Button
import com.sohohouse.seven.profile.view.model.SecondaryButton
import com.sohohouse.seven.profile.view.viewholder.SecondaryButtonViewHolder

class SecondaryButtonRenderer(
    private val onClick: (Button) -> Unit
) : Renderer<SecondaryButton, SecondaryButtonViewHolder> {

    override val type: Class<SecondaryButton>
        get() = SecondaryButton::class.java

    override fun createViewHolder(parent: ViewGroup): SecondaryButtonViewHolder {
        return SecondaryButtonViewHolder(
            ViewHolderSecondaryButtonBinding.bind(
                createItemView(parent, R.layout.view_holder_secondary_button)
            )
        )
    }

    override fun bindViewHolder(holder: SecondaryButtonViewHolder, item: SecondaryButton) {
        holder.bind(item, onClick)
    }
}