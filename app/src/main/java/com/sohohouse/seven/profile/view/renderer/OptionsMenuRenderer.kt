package com.sohohouse.seven.profile.view.renderer

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.ViewHolderOptionsMenuBinding
import com.sohohouse.seven.profile.view.model.Button
import com.sohohouse.seven.profile.view.model.OptionsMenu
import com.sohohouse.seven.profile.view.viewholder.OptionsMenuViewHolder

class OptionsMenuRenderer(
    private val onClick: (Button) -> Unit
) : Renderer<OptionsMenu, OptionsMenuViewHolder> {

    override val type: Class<OptionsMenu>
        get() = OptionsMenu::class.java

    override fun createViewHolder(parent: ViewGroup): OptionsMenuViewHolder {
        return OptionsMenuViewHolder(
            ViewHolderOptionsMenuBinding.bind(
                createItemView(parent, R.layout.view_holder_options_menu)
            )
        )
    }

    override fun bindViewHolder(holder: OptionsMenuViewHolder, item: OptionsMenu) {
        holder.bind(item, onClick)
    }
}