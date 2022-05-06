package com.sohohouse.seven.connect.filter.adapter

import android.view.View
import com.sohohouse.seven.R
import com.sohohouse.seven.base.GenericAdapter
import com.sohohouse.seven.common.extensions.context
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.connect.filter.base.SectionItem
import com.sohohouse.seven.databinding.ListFilterHeaderItemBinding

class SectionItemViewHolder(
    itemView: View,
    private val onSectionClick: (section: SectionItem, position: Int, expanded: Boolean) -> Unit
) : GenericAdapter.ViewHolder<SectionItem>(itemView) {

    private val binding = ListFilterHeaderItemBinding.bind(itemView)

    override fun bind(item: SectionItem) = binding.run {

        title.isSelected = item.expanded

        val selectedCount = item.items.count { it.selected }
        title.text = if (selectedCount > 0) {
            context.getString(
                R.string.name_and_count,
                getString(item.title),
                selectedCount.toString()
            )
        } else {
            getString(item.title)
        }

        root.setOnClickListener {
            item.expanded = item.expanded.not()
            title.isSelected = item.expanded

            if (item.items.isEmpty()) return@setOnClickListener

            onSectionClick(item, adapterPosition + 1, item.expanded)
        }
    }
}
