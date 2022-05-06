package com.sohohouse.seven.common.extensions

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.browsehouses.recycler.snaphelper.StartSnapHelper

fun StartSnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
    val layoutManager: RecyclerView.LayoutManager =
        recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
    val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
    return layoutManager.getPosition(snapView)

}