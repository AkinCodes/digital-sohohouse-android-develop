package com.sohohouse.seven.perks.landing.adapter

import android.view.View
import android.widget.ImageView
import ca.symbilityintersect.rendereradapter.BaseRenderer
import com.sohohouse.seven.R
import com.sohohouse.seven.perks.common.PerksViewHolder

class PerksItemRenderer(
    private val onItemClicked: (perkId: String, sharedImageView: ImageView) -> Unit
) : BaseRenderer<PerksItem, PerksViewHolder>(PerksItem::class.java) {

    override fun createViewHolder(itemView: View): PerksViewHolder = PerksViewHolder(itemView)

    override fun bindViewHolder(item: PerksItem, holder: PerksViewHolder) {
        holder.bind(item.item, item.venueName, onItemClicked)
    }

    override fun getLayoutResId(): Int = R.layout.view_perks_single_card
}