package com.sohohouse.seven.home.list

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.viewholders.ErrorStateListener
import com.sohohouse.seven.common.views.EventType
import com.sohohouse.seven.discover.housenotes.HouseNotesAdapter
import com.sohohouse.seven.home.browsehouses.BrowseHousesAdapterListener
import com.sohohouse.seven.home.browsehouses.viewholders.OurHousesViewHolder
import com.sohohouse.seven.home.completeyourprofile.SetUpAppPromptItem
import com.sohohouse.seven.home.adapter.viewholders.BannerCarouselViewHolder
import com.sohohouse.seven.home.adapter.viewholders.BannerShortcut
import com.sohohouse.seven.network.core.models.Event

interface AllHousesListener {
    fun onSeeAllButtonClicked()
}

interface StayWithUsListener {
    fun onStayWithUsButtonClicked()
}

interface LocalHouseHouseNotesAdapterListener : HouseNotesAdapter.Listener {
    fun onHouseNotesSeeAllClicked()
}

interface LocalHousePerksBottomLinkListener {
    fun onPerksLinkClicked()
}

interface LocalHouseBrowseHousesBottomLinkListener {
    fun onBrowseHousesLinkClicked()
}

interface LocalHouseHappeningNowListener {
    fun onHappeningNowEventClicked(
        eventId: String,
        sharedImageView: ImageView,
        eventType: EventType,
        event: Event
    )
}

interface PerksAdapterListener {
    fun onSeeAllPerkButtonClicked() {}
    fun onPerkClicked(id: String, title: String?, promoCode: String?)
}

interface LocalHouseSetUpAppPromptItemListener {
    fun onSetUpAppPromptItemClick(item: SetUpAppPromptItem)
}

interface OurHousesListener {
    fun onSeeAllClick()
    fun onHouseImageClick()
}

interface LocalHouseListener :
    LocalHouseHouseNotesAdapterListener,
    BrowseHousesAdapterListener,
    ErrorStateListener,
    AllHousesListener,
    LocalHouseHappeningNowListener,
    LocalHousePerksBottomLinkListener,
    LocalHouseBrowseHousesBottomLinkListener,
    LocalHouseSetUpAppPromptItemListener,
    OurHousesListener,
    PerksAdapterListener

@Deprecated("Use RenderAdapter instead")
class LocalHouseAdapter(private val listener: LocalHouseListener) : HouseAdapter() {

    var shortcutsListener: ((BannerShortcut) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BaseAdapterItem.getViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (item) {
            is BaseAdapterItem.HouseNoteItem -> item.attachListeners(listener)
            is BaseAdapterItem.BrowseHousesItem -> item.attachListeners(listener)
            is BaseAdapterItem.ErrorState -> item.attachListeners(listener)
            is BaseAdapterItem.AllHouses -> item.attachListeners(listener)
            is BaseAdapterItem.HappeningNowItem -> item.attachListeners(listener)
            is BaseAdapterItem.HouseBottomLinkItem -> item.attachListeners(listener, listener)
            is BaseAdapterItem.SetUpAppPromptItem -> item.attachListeners(listener)
            is BaseAdapterItem.OurHousesItem -> item.attachListeners(listener)
            is BaseAdapterItem.DiscoverPerks -> item.attachListeners(listener)
        }

        if (holder is BannerCarouselViewHolder) {
            holder.bind((item as BaseAdapterItem.BannerCarouselItem).shortcuts, shortcutsListener)
        } else {
            item.bindViewHolder(holder)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        for (item in currentItems) {
            when (item) {
                is BaseAdapterItem.HouseNoteItem -> item.detachListeners()
                is BaseAdapterItem.BrowseHousesItem -> item.detachListeners()
                is BaseAdapterItem.ErrorState -> item.detachListeners()
                is BaseAdapterItem.AllHouses -> item.detachListeners()
                is BaseAdapterItem.HouseBottomLinkItem -> item.detachListeners()
                is BaseAdapterItem.HappeningNowItem -> item.detachListeners()
                is BaseAdapterItem.SetUpAppPromptItem -> item.detachListeners()
                is BaseAdapterItem.OurHousesItem -> item.detachListeners()
                is BaseAdapterItem.DiscoverPerks -> item.detachListeners()
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is OurHousesViewHolder) {
            holder.onDetach()
        }
    }
}