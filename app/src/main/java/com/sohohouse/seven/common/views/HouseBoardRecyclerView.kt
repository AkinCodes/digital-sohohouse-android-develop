package com.sohohouse.seven.common.views

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class HouseBoardRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr), ExpandableListView {

    override var expanded: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            notifyListChanged()
        }

    private val listeners = mutableListOf<ExpandableListView.Listener>()

    override fun addItemDecoration(decor: ItemDecoration) {
        super.addItemDecoration(decor)
        registerListener(decor)
    }

    override fun setItemAnimator(animator: ItemAnimator?) {
        super.setItemAnimator(animator)
        registerListener(animator)
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        registerListener(adapter)
    }

    private fun registerListener(listener: Any?) {
        if (listener == null || listener !is ExpandableListView.Listener) return

        listeners.add(listener)
        notifyListChanged(listener)
    }

    private fun notifyListChanged() {
        listeners.forEach { notifyListChanged(it) }
    }

    private fun notifyListChanged(listener: ExpandableListView.Listener) {
        listener.onExpandableListChanged(expanded)
    }
}
