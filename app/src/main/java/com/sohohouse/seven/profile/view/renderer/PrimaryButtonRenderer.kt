package com.sohohouse.seven.profile.view.renderer

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.ViewHolderPrimaryButtonBinding
import com.sohohouse.seven.profile.view.model.Button
import com.sohohouse.seven.profile.view.model.PrimaryButton
import com.sohohouse.seven.profile.view.viewholder.PrimaryButtonViewHolder

class PrimaryButtonRenderer(
    private val onClick: (Button) -> Unit
) : Renderer<PrimaryButton, PrimaryButtonViewHolder> {

    override val type: Class<PrimaryButton>
        get() = PrimaryButton::class.java

    override fun createViewHolder(parent: ViewGroup): PrimaryButtonViewHolder {
        return PrimaryButtonViewHolder(
            ViewHolderPrimaryButtonBinding.bind(
                createItemView(parent, R.layout.view_holder_primary_button)
            )
        )
    }

    override fun bindViewHolder(holder: PrimaryButtonViewHolder, item: PrimaryButton) {
        holder.bind(item, onClick)
    }
}