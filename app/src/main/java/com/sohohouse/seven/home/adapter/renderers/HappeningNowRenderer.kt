package com.sohohouse.seven.home.adapter.renderers

import android.view.ViewGroup
import android.widget.ImageView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.home.adapter.viewholders.HappeningNowContentViewHolder
import com.sohohouse.seven.network.core.models.Event

class HappeningNowRenderer(private val listener: (Event, ImageView) -> Unit) :
    Renderer<BaseAdapterItem.HappeningNowItem.Container, HappeningNowContentViewHolder> {

    override val type: Class<BaseAdapterItem.HappeningNowItem.Container> =
        BaseAdapterItem.HappeningNowItem.Container::class.java

    override fun createViewHolder(parent: ViewGroup): HappeningNowContentViewHolder {
        return HappeningNowContentViewHolder(
            createItemView(
                parent,
                R.layout.local_house_happening_now_layout
            )
        )
    }

    override fun bindViewHolder(
        holder: HappeningNowContentViewHolder,
        item: BaseAdapterItem.HappeningNowItem.Container
    ) {
        item.bindViewHolder(holder, listener)
    }

}