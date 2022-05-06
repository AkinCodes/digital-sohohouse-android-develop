package com.sohohouse.seven.perks.landing.adapter

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.book.adapter.model.ListEvent
import com.sohohouse.seven.book.adapter.model.LoadingStateAdapterItem
import com.sohohouse.seven.home.houseboard.RendererDiffAdapter

class PerksLandingAdapter(private val onLastAllEventItem: () -> Unit = {}) : RendererDiffAdapter() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if (position == itemCount - 1 && mItems[position] is ListEvent) {
            onLastAllEventItem()
        }
    }

    /* used to add more events to all events after first page data */
    fun addAllEvents(allEvents: List<DiffItem>) {
        if (allEvents.isNotEmpty()) {
            mItems.addAll(allEvents)
        }
    }

    fun loadMore() {
        if (mItems.lastOrNull() != LoadingStateAdapterItem) {
            mItems.add(LoadingStateAdapterItem)
        }
    }

    fun loadFinished() {
        modifyList { items ->
            items.removeAll { it is LoadingStateAdapterItem }
        }
    }

}