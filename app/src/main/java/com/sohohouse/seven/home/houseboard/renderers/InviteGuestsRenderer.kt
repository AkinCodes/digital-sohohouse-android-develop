package com.sohohouse.seven.home.houseboard.renderers

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.renderers.SimpleRenderer
import com.sohohouse.seven.home.houseboard.items.InviteGuestsItem

class InviteGuestsRenderer(private val onClick: () -> Unit) :
    SimpleRenderer<InviteGuestsItem>(InviteGuestsItem::class.java) {
    override fun bindViewHolder(item: InviteGuestsItem, holder: RecyclerView.ViewHolder) {
        holder.itemView.clicks { onClick() }
    }

    override fun getLayoutResId() = R.layout.invite_guests_banner

}