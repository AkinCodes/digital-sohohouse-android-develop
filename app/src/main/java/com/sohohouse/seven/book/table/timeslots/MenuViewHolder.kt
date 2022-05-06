package com.sohohouse.seven.book.table.timeslots

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.context
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.common.extensions.startActivitySafely
import com.sohohouse.seven.common.navigation.IntentUtils
import com.sohohouse.seven.databinding.ItemMenuBinding
import com.sohohouse.seven.network.core.models.Menu

class MenuViewHolder(
    private val binding: ItemMenuBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Menu) = with(binding) {
        menuName.text = item.menuName
        menuImg.setImageFromUrl(item.menuImage?.largePng, isRound = true)
        root.clicks {
            context.startActivitySafely(
                IntentUtils.openUrlIntent(
                    item.menuUrl
                )
            )
        }
    }
}