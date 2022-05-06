package com.sohohouse.seven.connect.filter.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.base.GenericAdapter
import com.sohohouse.seven.connect.filter.base.*

class FilterAdapter(
    private val mode: FilterMode,
    private val listener: (Filter, Boolean) -> Unit
) : GenericAdapter<Filterable>() {

    private var _items: MutableList<Filterable> = mutableListOf()

    override var items: List<Filterable>
        get() = _items
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            _items = value.toMutableList()
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Filterable> {
        return when (viewType) {
            Filterable.SECTION_ITEM -> SectionItemViewHolder(
                inflateItemView(parent, R.layout.list_filter_header_item),
                ::onClickFilterSection
            ) as ViewHolder<Filterable>
            Filterable.FILTER_ITEM -> FilterItemViewHolder(
                inflateItemView(parent, R.layout.view_holder_list_filter_item),
                ::onClickFilterItem
            ) as ViewHolder<Filterable>
            else -> throw IllegalStateException("Unknown viewType of $viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder<Filterable>, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    override fun getItemCount(): Int {
        return items.map { it.size }.sum()
    }

    private fun onClickFilterSection(item: SectionItem, position: Int, expanded: Boolean) {
        if (expanded) {
            notifyItemRangeInserted(position, item.items.size)
        } else {
            notifyItemRangeRemoved(position, item.items.size)
        }
    }

    private fun onClickFilterItem(item: FilterItem) {
        when (mode) {
            FilterMode.TAGGING -> dispatchSelection(item.id, item.selected)
            FilterMode.FILTERS -> dispatchMultiSelection(item.id, item.selected)
        }
        listener(item.filter, item.selected)
    }

    private fun dispatchMultiSelection(id: String, selected: Boolean) {
        for (filterable in items) {
            if (filterable !is SectionItem) continue

            for (item in filterable.items) {
                if (item.id == id) {
                    item.selected = selected
                    notifyItemChanged(filterable)
                    break
                }
            }
        }
    }

    private fun dispatchSelection(id: String, selected: Boolean) {
        for (filterable in items) {
            if (filterable !is SectionItem) continue

            filterable.items.filter { it.id == id || it.selected }
                .takeIf { it.isNotEmpty() }
                ?.forEach { item ->
                    item.selected = if (item.id == id) selected else false
                    notifyItemChanged(item)
                }?.also {
                    notifyItemChanged(filterable)
                }
        }
    }

    private fun getItem(position: Int): Filterable {
        var start = 0
        var end: Int

        for (item in items) {
            end = start + item.size

            if (position in start until end) {
                val pos = position - start
                if (pos == 0) return item
                if (item is SectionItem && item.expanded) return item.items[pos - 1]
            }
            start = end
        }
        throw IndexOutOfBoundsException()
    }

    private fun getItemPosition(item: Filterable): Int {
        var start = 0
        var end: Int
        items.forEach {
            if (it == item) return start
            end = start + it.size
            if (it is SectionItem && it.expanded) {
                if (it.items.contains(item)) return start + it.items.indexOf(item) + 1
            }
            start = end
        }
        return -1
    }

    private fun notifyItemChanged(item: Filterable) {
        getItemPosition(item).takeIf { it >= 0 }?.let { notifyItemChanged(it) }
    }
}