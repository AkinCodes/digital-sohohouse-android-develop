package com.sohohouse.seven.home.adapter.renderers

import android.view.ViewGroup
import android.widget.TextView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.home.adapter.viewholders.HouseNotesSectionHeaderViewHolder

class HouseNotesHeaderRenderer(private val listener: () -> Unit = {}) :
    Renderer<BaseAdapterItem.HouseNoteItem.HouseNotesHeader, HouseNotesSectionHeaderViewHolder> {

    override val type: Class<BaseAdapterItem.HouseNoteItem.HouseNotesHeader> =
        BaseAdapterItem.HouseNoteItem.HouseNotesHeader::class.java

    override fun createViewHolder(parent: ViewGroup): HouseNotesSectionHeaderViewHolder {
        val itemView = createItemView(parent, R.layout.item_home_stories_header).apply {
            findViewById<TextView>(R.id.see_all)?.setOnClickListener { listener() }
        }
        return HouseNotesSectionHeaderViewHolder(itemView)
    }

    override fun bindViewHolder(
        holder: HouseNotesSectionHeaderViewHolder,
        item: BaseAdapterItem.HouseNoteItem.HouseNotesHeader
    ) {
    }
}