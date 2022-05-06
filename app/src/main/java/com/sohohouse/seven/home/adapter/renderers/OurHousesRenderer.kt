package com.sohohouse.seven.home.adapter.renderers

import android.view.ViewGroup
import android.widget.TextView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.common.views.SquareImageView
import com.sohohouse.seven.home.browsehouses.viewholders.OurHousesViewHolder

class OurHousesRenderer(
    private val onSeeAllClick: () -> Unit,
    private val onHouseImageClick: () -> Unit
) : Renderer<BaseAdapterItem.OurHousesItem, OurHousesViewHolder> {
    override val type: Class<BaseAdapterItem.OurHousesItem> =
        BaseAdapterItem.OurHousesItem::class.java

    override fun createViewHolder(parent: ViewGroup): OurHousesViewHolder {
        val itemView = createItemView(parent, R.layout.item_home_our_houses).apply {
            findViewById<TextView>(R.id.see_all)?.setOnClickListener { onSeeAllClick() }
            findViewById<SquareImageView>(R.id.our_houses_imageview)?.setOnClickListener { onHouseImageClick() }
        }
        return OurHousesViewHolder(itemView)
    }

    override fun bindViewHolder(holder: OurHousesViewHolder, item: BaseAdapterItem.OurHousesItem) {
        holder.bind(item)
    }

}