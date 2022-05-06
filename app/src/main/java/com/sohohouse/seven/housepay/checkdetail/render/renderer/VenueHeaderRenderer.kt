package com.sohohouse.seven.housepay.checkdetail.render.renderer

import android.view.ViewGroup
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.housepay.checkdetail.CheckItem
import com.sohohouse.seven.housepay.checkdetail.render.viewholder.CheckVenueNameViewHolder

class VenueHeaderRenderer : Renderer<CheckItem.VenueHeader, CheckVenueNameViewHolder> {
    override val type: Class<CheckItem.VenueHeader>
        get() = CheckItem.VenueHeader::class.java

    override fun createViewHolder(parent: ViewGroup): CheckVenueNameViewHolder {
        return CheckVenueNameViewHolder.create(parent)
    }

    override fun bindViewHolder(holder: CheckVenueNameViewHolder, item: CheckItem.VenueHeader) {
        holder.bind(item.venue)
    }
}