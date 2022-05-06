package com.sohohouse.seven.book.adapter.renderer

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.book.adapter.model.LoadingStateAdapterItem
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.common.viewholders.LoadingViewHolder

class LoadingStateRenderer : Renderer<LoadingStateAdapterItem, LoadingViewHolder> {

    override val type: Class<LoadingStateAdapterItem> = LoadingStateAdapterItem::class.java

    override fun createViewHolder(parent: ViewGroup): LoadingViewHolder {
        return LoadingViewHolder(createItemView(parent, R.layout.component_list_loading))
    }

    override fun bindViewHolder(holder: LoadingViewHolder, item: LoadingStateAdapterItem) {}

}