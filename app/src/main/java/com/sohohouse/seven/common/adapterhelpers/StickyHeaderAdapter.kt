package com.sohohouse.seven.common.adapterhelpers

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface StickyHeaderAdapter<VH : RecyclerView.ViewHolder> {

    companion object {
        const val NO_ID = -1
    }

    fun getHeaderId(position: Int): Int

    fun onCreateHeaderViewHolder(parent: ViewGroup): VH

    fun onBindHeaderViewHolder(holder: VH, position: Int) {}

}