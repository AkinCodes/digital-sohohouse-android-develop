@file:Suppress("SpellCheckingInspection")

package com.sohohouse.seven.common.design.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

inline fun <reified Item, ViewHolder : RecyclerView.ViewHolder> renderer(
    crossinline viewHolderProvider: (ViewGroup) -> ViewHolder,
    crossinline bind: (holder: ViewHolder, item: Item) -> Unit
): Renderer<Item, in ViewHolder> {
    return object : Renderer<Item, ViewHolder> {
        override val type: Class<Item>
            get() = Item::class.java

        override fun createViewHolder(parent: ViewGroup): ViewHolder {
            return viewHolderProvider(parent)
        }

        override fun bindViewHolder(holder: ViewHolder, item: Item) {
            bind(holder, item)
        }
    }
}

val NoOpBind = { _: RecyclerView.ViewHolder, _: Any -> }