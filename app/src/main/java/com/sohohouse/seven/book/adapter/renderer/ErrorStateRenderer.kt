package com.sohohouse.seven.book.adapter.renderer

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.book.adapter.model.ErrorStateAdapterItem
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.common.viewholders.ErrorStateViewHolder
import com.sohohouse.seven.databinding.ReloadableErrorStateViewholderLayoutBinding

class ErrorStateRenderer(private val onReloadButtonClicked: () -> Unit = {}) :
    Renderer<ErrorStateAdapterItem, ErrorStateViewHolder> {

    override val type: Class<ErrorStateAdapterItem> = ErrorStateAdapterItem::class.java

    override fun createViewHolder(parent: ViewGroup): ErrorStateViewHolder {
        return ErrorStateViewHolder(
            ReloadableErrorStateViewholderLayoutBinding.bind(
                createItemView(parent, R.layout.reloadable_error_state_viewholder_layout)
            )
        )
    }

    override fun bindViewHolder(holder: ErrorStateViewHolder, item: ErrorStateAdapterItem) {
        holder.reloadClicks { onReloadButtonClicked() }
    }

}