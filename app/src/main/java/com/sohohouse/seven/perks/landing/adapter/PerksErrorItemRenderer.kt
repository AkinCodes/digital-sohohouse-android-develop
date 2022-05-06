package com.sohohouse.seven.perks.landing.adapter

import android.view.View
import ca.symbilityintersect.rendereradapter.BaseRenderer
import com.sohohouse.seven.R
import com.sohohouse.seven.home.perks.LocalHousePerksErrorViewHolder

class PerksErrorItemRenderer :
    BaseRenderer<PerksErrorItem, LocalHousePerksErrorViewHolder>(PerksErrorItem::class.java) {

    override fun createViewHolder(itemView: View): LocalHousePerksErrorViewHolder =
        LocalHousePerksErrorViewHolder(itemView)

    override fun bindViewHolder(item: PerksErrorItem, holder: LocalHousePerksErrorViewHolder) {}

    override fun getLayoutResId(): Int = R.layout.view_perks_single_card_error
}