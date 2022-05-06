package com.sohohouse.seven.home.housenotes

import com.sohohouse.seven.R
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.home.adapter.viewholders.HOUSE_LANDING_SECTION_HEADER_LAYOUT
import com.sohohouse.seven.network.core.models.HouseNotes
import com.sohohouse.seven.network.sitecore.models.template.Template

class HouseNotesListFactory {

    companion object {

        fun getHomeHouseNotesList(templates: List<Template>): List<BaseAdapterItem.HouseNoteItem> {
            return mutableListOf<BaseAdapterItem.HouseNoteItem>().apply {
                add(BaseAdapterItem.HouseNoteItem.HouseNotesHeader)
                addAll(buildHouseNotes(templates))
            }
        }

        fun getHouseNotesHouseLanding(templates: List<Template>): List<BaseAdapterItem.HouseNoteItem> {
            return mutableListOf<BaseAdapterItem.HouseNoteItem>().apply {
                if (templates.isNotEmpty()) {
                    add(BaseAdapterItem.HouseNoteItem.MainHeader(HOUSE_LANDING_SECTION_HEADER_LAYOUT))
                    addAll(buildHouseNotes(templates))
                    add(BaseAdapterItem.HouseNoteItem.SeeAllButton())
                }
            }
        }

        fun getCityGuideContent(itemList: List<HouseNotes>): List<BaseAdapterItem.HouseNoteItem> {
            return mutableListOf<BaseAdapterItem.HouseNoteItem>().apply {
                if (itemList.isNotEmpty()) {
                    add(BaseAdapterItem.HouseNoteItem.SubHeader(R.string.home_city_guides_header))
                    addAll(getHouseNotes(itemList, true))
                }
            }
        }

        private fun buildHouseNotes(
            templates: List<Template>,
            isCityGuide: Boolean = false
        ): List<BaseAdapterItem.HouseNoteItem> {
            val items = ArrayList<BaseAdapterItem.HouseNoteItem>()
            if (templates.isNotEmpty()) {
                val template = templates.first()
                items.add(
                    BaseAdapterItem.HouseNoteItem.Content(
                        template.id, template.fieldValue.title,
                        if (template.fieldValue.thumbnailImageUrl.isNotEmpty()) template.fieldValue.thumbnailImageUrl else template.fieldValue.mainImageUrl,
                        template.fieldValue.mainVideo,
                        isCityGuide
                    )
                )
            }
            if (templates.size > 1) {
                val carouselItems = ArrayList<BaseAdapterItem.HouseNoteItem.Carousel.Item>()
                for (i in 1..templates.lastIndex) {
                    val template = templates[i]
                    carouselItems.add(
                        BaseAdapterItem.HouseNoteItem.Carousel.Item(
                            template.id, template.fieldValue.title,
                            if (template.fieldValue.thumbnailImageUrl.isNotEmpty()) template.fieldValue.thumbnailImageUrl else template.fieldValue.mainImageUrl,
                            template.fieldValue.mainVideo,
                            isCityGuide
                        )
                    )
                }
                items.add(BaseAdapterItem.HouseNoteItem.Carousel(carouselItems))
            }
            return items
        }

        fun getHouseNotes(
            itemList: List<HouseNotes>,
            isCityGuide: Boolean = false
        ): List<BaseAdapterItem.HouseNoteItem> {
            return itemList.map { note ->
                BaseAdapterItem.HouseNoteItem.Content(
                    note.id,
                    note.title,
                    note.headerImageLargePng,
                    note.headerVideoImageLarge,
                    isCityGuide
                )
            }
        }
    }
}
