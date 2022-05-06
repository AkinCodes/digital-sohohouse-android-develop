package com.sohohouse.seven.base

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

open class DefaultDiffItemCallback<T : DiffItem> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return if (oldItem.key != null && newItem.key != null)
            oldItem::class == newItem::class && oldItem.key == newItem.key
        else
            oldItem === newItem //default to reference equality to avoid clashes.
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}

open class DefaultDiffCallback<T : DiffItem>(
    private val oldList: List<T>,
    private val newList: List<T>
) : DiffUtil.Callback() {

    private val itemCallback = DefaultDiffItemCallback<T>()

    override fun areItemsTheSame(oldIndex: Int, newIndex: Int) =
        itemCallback.areItemsTheSame(oldList[oldIndex], newList[newIndex])

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areContentsTheSame(oldIndex: Int, newIndex: Int) =
        itemCallback.areContentsTheSame(oldList[oldIndex], newList[newIndex])
}

interface DiffItem {
    val key: Any? get() = null  // Items that have an ID or other unique key should provide this here
}