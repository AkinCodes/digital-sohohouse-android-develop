package com.sohohouse.seven.home.houseboard.renderers

import android.view.View
import ca.symbilityintersect.rendereradapter.BaseRenderer
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.ViewHolderNotificationBinding
import com.sohohouse.seven.home.houseboard.items.NotificationItem
import com.sohohouse.seven.home.houseboard.viewholders.NotificationViewHolder

class NotificationRenderer(private val listener: (NotificationItem) -> Unit) :
    BaseRenderer<NotificationItem, NotificationViewHolder>(NotificationItem::class.java) {

    override fun getLayoutResId(): Int = R.layout.view_holder_notification

    override fun createViewHolder(itemView: View): NotificationViewHolder {
        return NotificationViewHolder(ViewHolderNotificationBinding.bind(itemView))
    }

    override fun bindViewHolder(item: NotificationItem, holder: NotificationViewHolder) {
        holder.itemView.setOnClickListener {
            listener(item)
        }
        holder.bind(item)
    }
}
