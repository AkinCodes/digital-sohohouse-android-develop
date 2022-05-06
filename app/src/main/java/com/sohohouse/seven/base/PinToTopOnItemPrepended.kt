package com.sohohouse.seven.base

import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

class PinToTopOnItemPrepended(recyclerView: RecyclerView) : RecyclerView.AdapterDataObserver() {

    private val recyclerViewRef = WeakReference(recyclerView)

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)
        if (positionStart == 0) {
            recyclerViewRef.get()?.let {
                it.post {
                    it.scrollToPosition(0)
                }
            }
        }
    }
}