package com.sohohouse.seven.common.design.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.ComponentListItemBinding

class ListItemRenderer<T : ListItem>(
    override val type: Class<T>,
    private val listener: ((T, ImageView?, Int) -> Unit)? = null
) : Renderer<T, ListItemViewHolder<T>> {

    override fun createViewHolder(parent: ViewGroup): ListItemViewHolder<T> {
        return ListItemViewHolder(
            ComponentListItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun bindViewHolder(holder: ListItemViewHolder<T>, item: T) {
        holder.bind(item, listener)
    }
}