package com.sohohouse.seven.profile.edit.pronouns

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.renderers.SimpleBindingViewHolder
import com.sohohouse.seven.databinding.ItemPillListItemBinding

class PronounRenderer(val onSelected: (item: EditPronounsViewModel.PronounItem) -> Unit) :
    Renderer<EditPronounsViewModel.PronounItem, SimpleBindingViewHolder> {

    override val type: Class<EditPronounsViewModel.PronounItem>
        get() = EditPronounsViewModel.PronounItem::class.java

    override fun createViewHolder(parent: ViewGroup): SimpleBindingViewHolder {
        return SimpleBindingViewHolder(
            ItemPillListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun bindViewHolder(
        holder: SimpleBindingViewHolder,
        item: EditPronounsViewModel.PronounItem
    ) {
        with((holder.binding as? ItemPillListItemBinding) ?: return) {
            pillview.label = item.pronoun.name
            pillview.isActivated = item.isSelected
            root.clicks {
                onSelected(item)
            }
        }
    }

}