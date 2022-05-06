package com.sohohouse.seven.discover.housenotes

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sohohouse.seven.base.DefaultDiffItemCallback
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem.HouseNoteItem

class HouseNotesAdapter(private val houseNotesListAdapterListener: Listener) :
    PagedListAdapter<HouseNoteItem, ViewHolder>(DefaultDiffItemCallback()) {

    interface BaseListener {
        fun onHouseNoteClicked(id: String, isCityGuide: Boolean = false, position: Int)
    }

    interface Listener : BaseListener {
        fun onHouseNotesSeeAllClick()
        fun onDiscoverClick() {}
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.resLayout
            ?: throw IndexOutOfBoundsException("Item not found for position $position")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return BaseAdapterItem.getViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        item.bindViewHolder(holder)
        item.attachListeners(houseNotesListAdapterListener)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        try {
            (getItem(holder.adapterPosition) ?: return).detachListeners()
        } catch (e: Exception) {
        }
    }

}