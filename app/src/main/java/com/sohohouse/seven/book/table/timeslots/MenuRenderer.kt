package com.sohohouse.seven.book.table.timeslots

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.ItemMenuBinding
import com.sohohouse.seven.network.core.models.Menu

class MenuRenderer : Renderer<Menu, MenuViewHolder> {

    override val type: Class<Menu>
        get() = Menu::class.java

    override fun createViewHolder(parent: ViewGroup): MenuViewHolder {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun bindViewHolder(holder: MenuViewHolder, item: Menu) {
        holder.bind(item)
    }
}
