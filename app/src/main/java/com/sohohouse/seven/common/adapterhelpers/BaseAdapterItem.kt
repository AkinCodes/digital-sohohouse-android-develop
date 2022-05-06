package com.sohohouse.seven.common.adapterhelpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.resources
import com.sohohouse.seven.common.extensions.setPaddingBottom
import com.sohohouse.seven.common.extensions.setPaddingTop
import com.sohohouse.seven.common.viewholders.*
import com.sohohouse.seven.common.views.EventStatusType
import com.sohohouse.seven.common.views.EventType
import com.sohohouse.seven.common.views.carousel.CarouselEventAdapter
import com.sohohouse.seven.common.views.carousel.CarouselEventItems
import com.sohohouse.seven.common.views.carousel.CarouselEventItems.Companion.ITEM_TYPE_CONTENT
import com.sohohouse.seven.databinding.ItemHomeStoriesHeaderBinding
import com.sohohouse.seven.discover.housenotes.HouseNotesAdapter.Listener
import com.sohohouse.seven.home.adapter.viewholders.*
import com.sohohouse.seven.home.browsehouses.BrowseHousesAdapterListener
import com.sohohouse.seven.home.browsehouses.viewholders.*
import com.sohohouse.seven.home.completeyourprofile.*
import com.sohohouse.seven.home.housenotes.viewholders.HOUSE_NOTE_PERSONALIZE_LAYOUT
import com.sohohouse.seven.home.housenotes.viewholders.LocalHouseHouseNoteButtonViewHolder
import com.sohohouse.seven.home.list.*
import com.sohohouse.seven.home.perks.*
import com.sohohouse.seven.housenotes.viewholders.HOUSE_NOTES_CONTENT_LAYOUT
import com.sohohouse.seven.housenotes.viewholders.HouseNoteCarouselViewHolder
import com.sohohouse.seven.housenotes.viewholders.HouseNoteContentViewHolder
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.Perk
import com.sohohouse.seven.network.core.models.Venue
import com.sohohouse.seven.perks.common.PERKS_LAYOUT
import com.sohohouse.seven.perks.common.PerksViewHolder
import com.sohohouse.seven.perks.details.adapter.PerkItem

@Deprecated("DO NOT USE! Try to create new class whenever needed")
sealed class BaseAdapterItem(
    val resLayout: Int,
    viewHolderFactory: (View) -> RecyclerView.ViewHolder,
    val bgColorString: String = "",
    val bgColorResource: Int = -1
) : DiffItem {
    companion object {
        val layoutViewHolderMap = mutableMapOf<Int, (View) -> RecyclerView.ViewHolder>()

        fun getViewHolder(parent: ViewGroup, resLayout: Int): RecyclerView.ViewHolder {
            val viewHolderFactory = layoutViewHolderMap[resLayout]
            return viewHolderFactory?.invoke(
                LayoutInflater.from(parent.context).inflate(resLayout, parent, false)
            )
                ?: throw IllegalStateException("Unknown view type")
        }
    }

    init {
        layoutViewHolderMap[resLayout] = viewHolderFactory
    }

    open fun bindViewHolder(holder: RecyclerView.ViewHolder) {}

    sealed class HouseBottomLinkItem(
        resLayout: Int,
        viewHolderFactory: (View) -> RecyclerView.ViewHolder
    ) : BaseAdapterItem(resLayout, viewHolderFactory) {
        var perksListener: LocalHousePerksBottomLinkListener? = null
        var browseHousesListener: LocalHouseBrowseHousesBottomLinkListener? = null

        fun attachListeners(
            perksListener: LocalHousePerksBottomLinkListener?,
            browseHousesListener: LocalHouseBrowseHousesBottomLinkListener?
        ) {
            this.perksListener = perksListener
            this.browseHousesListener = browseHousesListener
        }

        fun detachListeners() {
            perksListener = null
            browseHousesListener = null
        }

        class Item :
            HouseBottomLinkItem(LOCAL_HOUSE_BOTTOM_LINK_LAYOUT, ::LocalHouseBottomLinkViewHolder) {
            override fun bindViewHolder(holder: RecyclerView.ViewHolder) {
                holder as LocalHouseBottomLinkViewHolder

                holder.bind(R.string.home_perks_cta, R.string.home_browse_houses_cta)
                holder.binding.perksBtn.clicks {
                    perksListener?.onPerksLinkClicked()
                }
                holder.binding.browseHousesBtn.clicks {
                    browseHousesListener?.onBrowseHousesLinkClicked()
                }
            }

            override val key: Any?
                get() = Item::class

        }
    }


    class OurHousesItem(@StringRes val subtitle: Int) :
        BaseAdapterItem(R.layout.item_home_our_houses, ::OurHousesViewHolder) {

        private var listener: OurHousesListener? = null

        val title = R.string.our_houses_title
        val description = R.string.our_houses_description

        val imageResIds = listOf(
            R.drawable.sh_barcelona_bedroom,
            R.drawable.sh_barcelona_rooftop,
            R.drawable.soho_rochouse,
            R.drawable.soho_farmhouse,
            R.drawable.soho_warehouse
        )

        override fun bindViewHolder(holder: RecyclerView.ViewHolder) {
            super.bindViewHolder(holder)
            with(holder as OurHousesViewHolder) {
                bind(this@OurHousesItem)
                headerBinding.seeAll.clicks { listener?.onSeeAllClick() }
                binding.ourHousesImageview.clicks { listener?.onHouseImageClick() }
            }

        }

        fun attachListeners(listener: LocalHouseListener) {
            this.listener = listener
        }

        fun detachListeners() {
            listener = null
        }

        override val key: Any?
            get() = javaClass
    }

    sealed class HouseNoteItem(
        resLayout: Int,
        viewHolderFactory: (View) -> RecyclerView.ViewHolder
    ) : BaseAdapterItem(resLayout, viewHolderFactory) {
        var listener: Listener? = null

        fun attachListeners(houseNotesListAdapterListener: Listener?) {
            this.listener = houseNotesListAdapterListener
        }

        fun detachListeners() {
            listener = null
        }

        data class MainHeader(@LayoutRes val layout: Int = LOCAL_HOUSE_SECTION_HEADER_LAYOUT) :
            HouseNoteItem(layout, ::HouseNotesSectionHeaderViewHolder) {
            override fun bindViewHolder(holder: RecyclerView.ViewHolder) {
                super.bindViewHolder(holder)
                (holder as HouseNotesSectionHeaderViewHolder).bind(R.string.home_house_notes_header)
            }

            override val key: Any?
                get() = MainHeader::class
        }

        object HouseNotesHeader : HouseNoteItem(
            R.layout.item_home_stories_header,
            { view -> object : RecyclerView.ViewHolder(view) {} }) {

            override fun bindViewHolder(holder: RecyclerView.ViewHolder) {
                val binding = ItemHomeStoriesHeaderBinding.bind(holder.itemView)
                with(binding.includedSectionHeader) {
                    seeAll.setOnClickListener { listener?.onHouseNotesSeeAllClick() }
                    title.setText(R.string.home_house_notes_header)
                    subtitle.setText(R.string.home_house_notes_subheader)
                }
            }

            override val key: Any?
                get() = HouseNotesHeader::class.java
        }

        data class SubHeader(val titleRes: Int) :
            HouseNoteItem(LIST_DIVIDER_LAYOUT, ::ListSectionHeaderViewHolder) {

            override fun bindViewHolder(holder: RecyclerView.ViewHolder) {
                super.bindViewHolder(holder)
                (holder as ListSectionHeaderViewHolder).let {
                    it.binding.root.setPaddingTop(holder.resources.getDimensionPixelOffset(R.dimen.dp_24))
                    it.binding.root.setPaddingBottom(holder.resources.getDimensionPixelOffset(R.dimen.dp_6))
                    it.bind(this)
                }
            }
        }

        data class Content constructor(
            val id: String,
            val title: String,
            val imageUrl: String,
            val videoImageUrl: String,
            val isCityGuide: Boolean = false
        ) : HouseNoteItem(HOUSE_NOTES_CONTENT_LAYOUT, ::HouseNoteContentViewHolder) {
            override fun bindViewHolder(holder: RecyclerView.ViewHolder) {
                (holder as HouseNoteContentViewHolder).bind(this)
                holder.setOnClickListener {
                    listener?.onHouseNoteClicked(id, isCityGuide, holder.adapterPosition)
                }
            }

            override val key: Any?
                get() = id
        }

        data class Carousel(val houseNotes: ArrayList<Item>) :
            HouseNoteItem(R.layout.item_home_house_notes_carousel, ::HouseNoteCarouselViewHolder) {
            override fun bindViewHolder(holder: RecyclerView.ViewHolder) {
                (holder as HouseNoteCarouselViewHolder).bind(this) { id, cityGuide, position ->
                    listener?.onHouseNoteClicked(
                        id,
                        cityGuide,
                        position = position
                    )
                }
            }

            data class Item(
                val id: String,
                val title: String,
                val imageUrl: String,
                val videoImageUrl: String,
                private val isCityGuide: Boolean = false
            ) : DiffItem {
                override val key: Any?
                    get() = id
            }

        }

        class SeeAllButton :
            HouseNoteItem(HOUSE_NOTE_PERSONALIZE_LAYOUT, ::LocalHouseHouseNoteButtonViewHolder) {
            override fun bindViewHolder(holder: RecyclerView.ViewHolder) {
                super.bindViewHolder(holder)
                (holder as LocalHouseHouseNoteButtonViewHolder).setButtonClickListener {
                    listener?.onDiscoverClick()
                }
            }

            override val key: Any?
                get() = SeeAllButton::class

        }

        data class ResultsEmpty(
            val titleStringRes: Int = R.string.content_filter_empty_header,
            val supportingStringRes: Int = R.string.content_filter_empty_supporting
        ) : HouseNoteItem(ZERO_STATE_INFO_LAYOUT, ::FilterZeroStateViewHolder) {
            override fun bindViewHolder(holder: RecyclerView.ViewHolder) {
                super.bindViewHolder(holder)
                (holder as FilterZeroStateViewHolder).bind(this)
            }
        }
    }

    class AllHouses :
        BaseAdapterItem(LOCAL_HOUSE_ALL_HOUSES_LAYOUT, ::LocalHouseAllHousesViewHolder) {
        var seeAllListener: AllHousesListener? = null

        fun attachListeners(seeAllListener: AllHousesListener) {
            this.seeAllListener = seeAllListener
        }

        fun detachListeners() {
            seeAllListener = null
        }

        override fun bindViewHolder(holder: RecyclerView.ViewHolder) {
            super.bindViewHolder(holder)
            (holder as LocalHouseAllHousesViewHolder).setBookingBtnClickListener { seeAllListener?.onSeeAllButtonClicked() }
        }

        override val key: Any?
            get() = AllHouses::class

    }

    class Perks(
        private val perks: Perk,
        private val relatedVenueName: String = ""
    ) : BaseAdapterItem(PERKS_LAYOUT, ::PerksViewHolder) {

        var perksAdapterListener: PerksAdapterListener? = null

        fun attachListeners(perksAdapterListener: PerksAdapterListener) {
            this.perksAdapterListener = perksAdapterListener
        }

        fun detachListeners() {
            perksAdapterListener = null
        }

        override fun bindViewHolder(holder: RecyclerView.ViewHolder) {
            super.bindViewHolder(holder)

            (holder as PerksViewHolder).bind(
                item = perks,
                associatedVenueName = relatedVenueName
            ) { _, _ ->
                perksAdapterListener?.onPerkClicked(perks.id, perks.title, perks.promotionCode)
            }
        }
    }

    class InvalidPerks : BaseAdapterItem(PERKS_ERROR_LAYOUT, ::LocalHousePerksErrorViewHolder)

    class PerksSeeAll : BaseAdapterItem(PERKS_SEE_ALL_LAYOUT, ::LocalHousePerksSeeAllViewHolder) {
        var perksAdapterListener: PerksAdapterListener? = null

        fun attachListeners(perksAdapterListener: PerksAdapterListener) {
            this.perksAdapterListener = perksAdapterListener
        }

        fun detachListeners() {
            perksAdapterListener = null
        }

        override fun bindViewHolder(holder: RecyclerView.ViewHolder) {
            super.bindViewHolder(holder)
            (holder as LocalHousePerksSeeAllViewHolder).bind { perksAdapterListener?.onSeeAllPerkButtonClicked() }
        }
    }

    class DiscoverPerks(val perks: List<PerksItem>) :
        BaseAdapterItem(DISCOVER_PERKS_LAYOUT, ::DiscoverPerksViewHolder) {

        override val key: Any?
            get() = DiscoverPerks::class.java.simpleName

        private var listener: PerksAdapterListener? = null

        fun attachListeners(listener: PerksAdapterListener) {
            this.listener = listener
        }

        fun detachListeners() {
            listener = null
        }

        override fun bindViewHolder(holder: RecyclerView.ViewHolder) {
            super.bindViewHolder(holder)
            (holder as DiscoverPerksViewHolder).bind(this,
                { listener?.onSeeAllPerkButtonClicked() },
                { id, title, promoCode -> listener?.onPerkClicked(id, title, promoCode) })
        }

        override fun equals(other: Any?): Boolean {
            if (other !is DiscoverPerks) return false
            return perks.none { !other.perks.contains(it) }
        }

        override fun hashCode(): Int {
            var result = key?.hashCode() ?: 0
            result = 31 * result + perks.hashCode()
            return result
        }

        class PerksItem(
            override val id: String,
            override val city: String?,
            override val category: String?,
            override val onlineOnly: Boolean?,
            override val title: String?,
            override val summary: String?,
            override val promotionCode: String?,
            override val imageUrl: String?
        ) : DiffItem, PerkItem {
            constructor(perk: Perk) : this(
                perk.id,
                perk.city,
                perk.contentPillar,
                perk.onlineOnly,
                perk.title,
                perk.summary,
                perk.promotionCode,
                perk.headerImageLarge
            )

            override val key: Any?
                get() = id

            override fun equals(other: Any?): Boolean {
                return other is PerksItem
                        && id == other.id
                        && summary == other.summary
                        && title == other.title
                        && imageUrl == other.imageUrl
                        && city == other.city
                        && category == other.category
                        && onlineOnly == other.onlineOnly
            }

            override fun hashCode(): Int {
                var result = id.hashCode()
                result = 31 * result + (title?.hashCode() ?: 0)
                result = 31 * result + (summary?.hashCode() ?: 0)
                result = 31 * result + (imageUrl?.hashCode() ?: 0)
                result = 31 * result + (city?.hashCode() ?: 0)
                result = 31 * result + (category?.hashCode() ?: 0)
                result = 31 * result + (onlineOnly.hashCode() ?: 0)
                return result
            }
        }
    }

    enum class BrowseHousesItemType {
        REGION_HEADER,
        CONTENT
    }

    sealed class BrowseHousesItem(
        val type: BrowseHousesItemType,
        resLayout: Int,
        viewHolderFactory: (View) -> RecyclerView.ViewHolder
    ) : BaseAdapterItem(resLayout, viewHolderFactory) {
        var browseHouseListener: BrowseHousesAdapterListener? = null

        fun attachListeners(browseHouseListener: BrowseHousesAdapterListener) {
            this.browseHouseListener = browseHouseListener
        }

        fun detachListeners() {
            browseHouseListener = null
        }

        data class RegionHeader(val titleRes: Int) : BrowseHousesItem(
            BrowseHousesItemType.REGION_HEADER,
            LOCAL_HOUSE_BROWSE_HOUSES_REGION_HEADER_LAYOUT,
            ::LocalHouseBrowseHousesRegionHeaderViewHolder
        ) {
            override fun bindViewHolder(holder: RecyclerView.ViewHolder) {
                super.bindViewHolder(holder)
                (holder as LocalHouseBrowseHousesRegionHeaderViewHolder).bind(this)
            }
        }

        data class Content(val house: Venue, val isLastItem: Boolean = false) : BrowseHousesItem(
            BrowseHousesItemType.CONTENT,
            LOCAL_HOUSE_BROWSE_HOUSES_CONTENT_LAYOUT,
            ::LocalHouseBrowseHousesContentViewHolder
        ) {
            override fun bindViewHolder(holder: RecyclerView.ViewHolder) {
                super.bindViewHolder(holder)
                (holder as LocalHouseBrowseHousesContentViewHolder).let {
                    it.bind(this)
                    it.setHouseClickListener {
                        browseHouseListener?.onHomeClicked(
                            house.id,
                            holder.binding.browseHouseImage,
                            house
                        )
                    }
                }
            }

            override val key: Any?
                get() = house.name
        }
    }

    class BannerCarouselItem(val shortcuts: Array<BannerShortcut>) :
        BaseAdapterItem(BANNER_CAROUSEL_LAYOUT, ::BannerCarouselViewHolder) {
        override val key: Any?
            get() = BannerCarouselItem::class
    }

    sealed class DateHeaderItem(
        resLayout: Int,
        viewHolderFactory: (View) -> RecyclerView.ViewHolder
    ) : BaseAdapterItem(resLayout, viewHolderFactory) {
        data class Header(val dateText: String) :
            DateHeaderItem(LOCAL_HOUSE_DATE_HEADER_LAYOUT, ::LocalHouseDateHeaderViewHolder) {
            override fun bindViewHolder(holder: RecyclerView.ViewHolder) {
                super.bindViewHolder(holder)
                (holder as LocalHouseDateHeaderViewHolder).bind(dateText)
            }

            override val key: Any?
                get() = Header::class
        }
    }

    sealed class HappeningNowItem(
        resLayout: Int,
        viewHolderFactory: (View) -> RecyclerView.ViewHolder
    ) : BaseAdapterItem(resLayout, viewHolderFactory) {

        var happeningNowListener: LocalHouseHappeningNowListener? = null

        fun attachListeners(happeningNowListener: LocalHouseHappeningNowListener) {
            this.happeningNowListener = happeningNowListener
        }

        fun detachListeners() {
            happeningNowListener = null
        }

        data class Container(
            val dataItems: MutableList<CarouselEventItems>,
            private val headerText: String = "",
            private val captionText: String = "",
            val isDynamicHouseCarousel: Boolean = false
        ) : HappeningNowItem(LOCAL_HOUSE_HAPPENING_NOW_LAYOUT, ::HappeningNowContentViewHolder) {

            private lateinit var adapter: CarouselEventAdapter

            override fun bindViewHolder(holder: RecyclerView.ViewHolder) {
                super.bindViewHolder(holder)

                adapter = CarouselEventAdapter(dataItems) { event, sharedView ->
                    happeningNowListener?.onHappeningNowEventClicked(
                        eventId = event.id,
                        sharedImageView = sharedView,
                        eventType = EventType.get(event.type),
                        event = event
                    )
                }

                (holder as HappeningNowContentViewHolder).bind(headerText, captionText, adapter)
            }

            fun bindViewHolder(
                holder: RecyclerView.ViewHolder,
                onClick: (event: Event, sharedImageView: ImageView) -> Unit
            ) {
                adapter = CarouselEventAdapter(dataItems, onClick)
                (holder as HappeningNowContentViewHolder).bind(headerText, captionText, adapter)
            }

            fun updateNestedItem(event: Event) {
                adapter.updateItem(event)
            }

            override val key: Any?
                get() = headerText
        }

        class Content(
            override var event: Event,
            override val eventStatusType: EventStatusType,
            override val venueTimeZone: String,
            override val venueName: String,
            override val venueColor: String,
            override val itemType: Int = ITEM_TYPE_CONTENT
        ) : CarouselEventItems {
            override val key: Any?
                get() = event.id
        }
    }

    sealed class SetUpAppPromptItem(
        resLayout: Int,
        viewHolderFactory: (View) -> RecyclerView.ViewHolder
    ) : BaseAdapterItem(resLayout, viewHolderFactory) {
        var setUpAppPromptItemListener: LocalHouseSetUpAppPromptItemListener? = null

        fun attachListeners(setUpAppPromptItemListener: LocalHouseSetUpAppPromptItemListener) {
            this.setUpAppPromptItemListener = setUpAppPromptItemListener
        }

        fun detachListeners() {
            setUpAppPromptItemListener = null
        }

        class Container(val dataItems: List<com.sohohouse.seven.home.completeyourprofile.SetUpAppPromptItem>) :
            SetUpAppPromptItem(
                SET_UP_APP_PROMPTS_CONTAINER_LAYOUT,
                ::SetUpAppPromptContainerViewHolder
            ) {
            private lateinit var adapter: SetUpAppPromptsCarouselAdapter
            override fun bindViewHolder(holder: RecyclerView.ViewHolder) {
                super.bindViewHolder(holder)

                adapter = SetUpAppPromptsCarouselAdapter(SET_UP_APP_PROMPT_ITEM_LAYOUT, dataItems)
                { item -> setUpAppPromptItemListener?.onSetUpAppPromptItemClick(item) }
                (holder as SetUpAppPromptContainerViewHolder).bind(adapter)
            }

            fun bindViewHolder(
                holder: RecyclerView.ViewHolder,
                onClick: (item: com.sohohouse.seven.home.completeyourprofile.SetUpAppPromptItem) -> Unit
            ) {
                adapter = SetUpAppPromptsCarouselAdapter(
                    SET_UP_APP_PROMPT_ITEM_LAYOUT,
                    dataItems,
                    onClick
                )
                (holder as SetUpAppPromptContainerViewHolder).bind(adapter)
            }
        }

        override val key: Any?
            get() = Container::class
    }

    class Loading : BaseAdapterItem(R.layout.component_list_loading, ::LoadingViewHolder) {
        override val key: Any?
            get() = Loading::class
    }

    class ErrorState : BaseAdapterItem(ERROR_STATE_LAYOUT, ::ErrorStateOldViewHolder) {
        private var errorStateListener: ErrorStateListener? = null

        fun attachListeners(errorStateListener: ErrorStateListener) {
            this.errorStateListener = errorStateListener
        }

        fun detachListeners() {
            errorStateListener = null
        }

        override fun bindViewHolder(holder: RecyclerView.ViewHolder) {
            super.bindViewHolder(holder)
            (holder as ErrorStateOldViewHolder).reloadClicks { errorStateListener?.onReloadButtonClicked() }
        }
    }
}