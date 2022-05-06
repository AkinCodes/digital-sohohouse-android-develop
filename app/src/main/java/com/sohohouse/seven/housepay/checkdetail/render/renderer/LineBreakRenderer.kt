package com.sohohouse.seven.housepay.checkdetail.render.renderer

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.common.extensions.layoutInflater
import com.sohohouse.seven.common.renderers.SimpleViewHolder
import com.sohohouse.seven.housepay.checkdetail.CheckItem

class LineBreakRenderer : Renderer<CheckItem.LineBreak, RecyclerView.ViewHolder> {
    override val type: Class<CheckItem.LineBreak>
        get() = CheckItem.LineBreak::class.java

    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return SimpleViewHolder(
            parent.layoutInflater().inflate(
                R.layout.item_check_line_break,
                parent,
                false
            )
        )
    }

    override fun bindViewHolder(holder: RecyclerView.ViewHolder, item: CheckItem.LineBreak) {
    }

}