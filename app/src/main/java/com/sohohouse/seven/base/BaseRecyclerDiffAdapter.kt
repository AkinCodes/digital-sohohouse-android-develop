package com.sohohouse.seven.base

import android.annotation.SuppressLint
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerDiffAdapter<VH : RecyclerView.ViewHolder, T : DiffItem>(items: List<T> = emptyList()) :
    RecyclerView.Adapter<VH>() {

    var currentItems: List<T> = items
        private set

    protected open val listUpdateCallback get() = AdapterListUpdateCallback(this)

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<T>, performDiffing: Boolean = true) {
        if (performDiffing) {
            DiffUtil.calculateDiff(DefaultDiffCallback(currentItems, list)).let {
                this.currentItems = list
                it.dispatchUpdatesTo(listUpdateCallback)
            }
        } else {
            this.currentItems = list
            notifyDataSetChanged()
        }
    }

    fun getItem(index: Int) = currentItems[index]

    override fun getItemCount() = currentItems.size

    fun remove(item: T) {
        modifyList {
            it.remove(item)
        }
    }

    fun removeAt(index: Int) {
        modifyList {
            it.removeAt(index)
        }
    }

    fun removeAll(items: List<T>) {
        modifyList {
            it.removeAll(items)
        }
    }

    fun add(item: T) {
        modifyList {
            it.add(item)
        }
    }

    fun addAt(index: Int, item: T) {
        modifyList {
            it.add(index, item)
        }
    }

    fun addAll(items: List<T>) {
        modifyList {
            it.addAll(items)
        }
    }

    fun addAllAt(index: Int, items: List<T>) {
        modifyList {
            it.addAll(index, items)
        }
    }

    //Add, remove, or replace items in the list. Do not mutate individual items; it will have no effect
    protected fun modifyList(function: (items: ArrayList<T>) -> Unit) {
        submitList(ArrayList(currentItems).apply {
            function(this)
        })
    }
}