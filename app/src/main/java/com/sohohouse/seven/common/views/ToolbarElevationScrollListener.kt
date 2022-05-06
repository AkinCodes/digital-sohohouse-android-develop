package com.sohohouse.seven.common.views

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.sohohouse.seven.R
import java.lang.ref.WeakReference

//Adds elevation to a toolbar when content has been scrolled
open class ToolbarElevationScrollListener(private val toolbar: WeakReference<View>) :
    RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (recyclerView.canScrollVertically(-1)) {
            toolbar.get()?.let {
                it.elevation = it.resources.getDimensionPixelOffset(R.dimen.dp_4).toFloat()
            }
        } else {
            toolbar.get()?.let { it.elevation = 0f }
        }
    }
}