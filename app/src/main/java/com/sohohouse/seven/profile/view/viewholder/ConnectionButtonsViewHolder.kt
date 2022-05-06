package com.sohohouse.seven.profile.view.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.sohohouse.seven.R
import com.sohohouse.seven.common.design.adapter.RendererDiffAdapter
import com.sohohouse.seven.common.design.list.ListItemPaddingDecoration
import com.sohohouse.seven.databinding.ViewHolderConnectionButtonsBinding
import com.sohohouse.seven.profile.view.model.*
import com.sohohouse.seven.profile.view.renderer.OptionsMenuRenderer
import com.sohohouse.seven.profile.view.renderer.PrimaryButtonRenderer
import com.sohohouse.seven.profile.view.renderer.SecondaryButtonRenderer

class ConnectionButtonsViewHolder(
    binding: ViewHolderConnectionButtonsBinding,
    onClickButton: (Button) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val adapter = object : RendererDiffAdapter<Button>() {
        init {
            registerRenderers(
                PrimaryButtonRenderer(onClickButton),
                SecondaryButtonRenderer(onClickButton),
                OptionsMenuRenderer(onClickButton)
            )
        }

        override fun getItemViewType(position: Int): Int {
            return when (items[position]) {
                is PrimaryButton -> PrimaryButton::class.hashCode()
                is SecondaryButton -> SecondaryButton::class.hashCode()
                is OptionsMenu -> OptionsMenu::class.hashCode()
            }
        }
    }

    init {
        with(binding.recyclerView) {
            adapter = this@ConnectionButtonsViewHolder.adapter
            layoutManager = FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.CENTER
            }
            addItemDecoration(
                ListItemPaddingDecoration(
                    orientation = RecyclerView.HORIZONTAL,
                    horizontalSpacing = resources.getDimensionPixelSize(R.dimen.dp_8)
                )
            )
        }
    }

    fun bind(buttons: Buttons) {
        adapter.submitItems(buttons.list)
    }

}