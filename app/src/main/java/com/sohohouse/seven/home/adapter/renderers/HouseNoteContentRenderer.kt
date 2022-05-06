package com.sohohouse.seven.home.adapter.renderers

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.housenotes.viewholders.HouseNoteContentViewHolder

class HouseNoteContentRenderer(
    private val onHouseNoteClicked: (id: String, isCityGuide: Boolean, position: Int) -> Unit
) : Renderer<BaseAdapterItem.HouseNoteItem.Content, HouseNoteContentViewHolder> {

    override val type: Class<BaseAdapterItem.HouseNoteItem.Content> =
        BaseAdapterItem.HouseNoteItem.Content::class.java

    override fun createViewHolder(parent: ViewGroup): HouseNoteContentViewHolder {
        return HouseNoteContentViewHolder(
            createItemView(
                parent,
                R.layout.local_house_house_note_content_layout
            )
        )
    }

    override fun bindViewHolder(
        holder: HouseNoteContentViewHolder,
        item: BaseAdapterItem.HouseNoteItem.Content
    ) {
        holder.bind(item, onHouseNoteClicked)
    }

}