package com.sohohouse.seven.common.design.textblock

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.TextBlockSmallHeaderBinding

class SmallTextBlockRenderer<T : TextBlock>(override val type: Class<T>) :
    Renderer<T, SmallTextBlockViewHolder> {

    override fun createViewHolder(parent: ViewGroup): SmallTextBlockViewHolder {
        return SmallTextBlockViewHolder(
            TextBlockSmallHeaderBinding.bind(
                createItemView(parent, R.layout.text_block_small_header)
            )
        )
    }

    override fun bindViewHolder(holder: SmallTextBlockViewHolder, item: T) {
        holder.bind(item)
    }
}