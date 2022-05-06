package com.sohohouse.seven.home.adapter.renderers

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.housenotes.viewholders.HouseNoteCarouselViewHolder

class HouseNotesCarouselRenderer(
    private val onHouseNoteClicked: (id: String, isCityGuide: Boolean, position: Int) -> Unit
) : Renderer<BaseAdapterItem.HouseNoteItem.Carousel, HouseNoteCarouselViewHolder> {

    override val type: Class<BaseAdapterItem.HouseNoteItem.Carousel> =
        BaseAdapterItem.HouseNoteItem.Carousel::class.java

    override fun createViewHolder(parent: ViewGroup): HouseNoteCarouselViewHolder {
        return HouseNoteCarouselViewHolder(
            createItemView(
                parent,
                R.layout.item_home_house_notes_carousel
            )
        )
    }

    override fun bindViewHolder(
        holder: HouseNoteCarouselViewHolder,
        item: BaseAdapterItem.HouseNoteItem.Carousel
    ) {
        holder.bind(item, onHouseNoteClicked)
    }

}