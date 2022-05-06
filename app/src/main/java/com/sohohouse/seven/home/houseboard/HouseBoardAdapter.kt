package com.sohohouse.seven.home.houseboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ca.symbilityintersect.rendereradapter.RendererAdapter
import com.sohohouse.seven.base.DefaultDiffCallback
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.adapterhelpers.StickyHeaderAdapter
import com.sohohouse.seven.common.views.ExpandableListView
import com.sohohouse.seven.databinding.ViewHolderNotificationMenuBinding
import com.sohohouse.seven.home.houseboard.SwipeCallback.SwipeHelper
import com.sohohouse.seven.home.houseboard.items.NotificationItem
import kotlin.math.min

class HouseBoardAdapter(
    private val showLessListener: () -> Unit,
    private val menuListener: () -> Unit
) : RendererAdapter(),
    StickyHeaderAdapter<HouseBoardAdapter.HeaderViewHolder>,
    ExpandableListView.Listener,
    SwipeHelper<NotificationItem> {

    companion object {
        private const val STACKED_ITEM_SIZE = 3
        const val NOTIFICATION_HEADER_ID = 0
    }

    private var notifications = listOf<NotificationItem>()

    private var stackPosition: Int = 0

    val notificationItemCount: Int
        get() = notifications.size

    private val visibleNotifications: List<NotificationItem>
        get() = if (expanded)
            notifications
        else
            notifications.subList(0, min(STACKED_ITEM_SIZE, notifications.size))

    var expanded: Boolean = false
        set(value) {
            if (field == value) return
            field = value

            setNotifications(notifications)
            notifyItemRangeChanged(stackPosition, visibleNotifications.size)
        }

    private fun setNotifications(notifications: List<NotificationItem>) {
        synchronized(mItems) {
            val items =
                mItems.filter { it !is NotificationItem }.toMutableList() as MutableList<DiffItem>

            if (notifications.isNotEmpty()) {
                items.addAll(stackPosition, visibleNotifications)
            }

            DiffUtil.calculateDiff(DefaultDiffCallback(mItems as List<DiffItem>, items)).run {
                mItems = items as List<DiffItem>
                dispatchUpdatesTo(this@HouseBoardAdapter)
            }
        }
    }

    override fun setItems(list: List<Any>) {
        synchronized(mItems) {
            notifications = list.filterIsInstance<NotificationItem>()

            val items = list.toMutableList() as MutableList<DiffItem>
            if (notifications.isNotEmpty()) {
                stackPosition = items.indexOf(notifications.first())
                items.removeAll(notifications)
                items.addAll(stackPosition, visibleNotifications)
            }

            DiffUtil.calculateDiff(DefaultDiffCallback(mItems as List<DiffItem>, items)).run {
                mItems = items as List<DiffItem>
                dispatchUpdatesTo(this@HouseBoardAdapter)
            }
        }
    }

    /**
     * NotificationSwipeCallback
     */
    override fun canSwipe(position: Int): Boolean {
        if (getItem(position) == null) return false
        return expanded || notifications.size == 1
    }

    override fun getItem(position: Int): NotificationItem? {
        return try {
            mItems[position]?.takeIf { it is NotificationItem } as? NotificationItem
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    }

    /**
     * ExpandableListView.Listener
     */
    override fun onExpandableListChanged(expanded: Boolean) {
        this.expanded = expanded
    }

    /**
     * StickyHeaderAdapter
     */
    override fun getHeaderId(position: Int): Int {
        return if (expanded && stackPosition <= position && position < notifications.size)
            NOTIFICATION_HEADER_ID
        else
            StickyHeaderAdapter.NO_ID
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): HeaderViewHolder {
        return HeaderViewHolder(
            ViewHolderNotificationMenuBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    inner class HeaderViewHolder(binding: ViewHolderNotificationMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.showLess.setOnClickListener { showLessListener() }
            binding.options.setOnClickListener { menuListener() }
        }
    }
}
