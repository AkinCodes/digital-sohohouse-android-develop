package com.sohohouse.seven.base

import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

open class PinToTopAdapterDataObserver(private val recyclerView: WeakReference<RecyclerView>) :
    RecyclerView.AdapterDataObserver() {

    private fun goToTopOfRecyclerView() {
        recyclerView.get()?.scrollToPosition(0)
        onPinnedToTop()
    }

    protected open fun onPinnedToTop() {
        // default do nothing
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        goToTopOfRecyclerView()
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        goToTopOfRecyclerView()
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        goToTopOfRecyclerView()
    }

}