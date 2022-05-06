package com.sohohouse.seven.housepay.checkdetail.render.viewholder

import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.common.design.adapter.RendererDiffAdapter
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.context
import com.sohohouse.seven.common.extensions.layoutInflater
import com.sohohouse.seven.databinding.ItemSelectTipBinding
import com.sohohouse.seven.databinding.ItemTipOptionBinding
import com.sohohouse.seven.housepay.checkdetail.TipOptionUiModel
import com.sohohouse.seven.housepay.checkdetail.Tips

class SelectTipViewHolder private constructor(
    private val binding: ItemSelectTipBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup): SelectTipViewHolder {
            return SelectTipViewHolder(
                ItemSelectTipBinding.inflate(
                    parent.layoutInflater(),
                    parent,
                    false
                )
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val adapter: RendererDiffAdapter<TipOptionUiModel>
        get() = binding.itemTipSelectRv.adapter as RendererDiffAdapter<TipOptionUiModel>

    init {
        binding.itemTipSelectRv.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.itemTipSelectRv.adapter = RendererDiffAdapter<TipOptionUiModel>().apply {
            registerRenderers(
                tipItemRenderer<TipOptionUiModel.PercentageTipOptionUiModel>(),
                tipItemRenderer<TipOptionUiModel.CustomTipOptionUiModel>()
            )
        }
        binding.itemTipSelectRv.itemAnimator = null
    }

    fun bind(item: Tips) {
        adapter.submitItems(item.tipOptions)
    }
}

private inline fun <reified T : TipOptionUiModel> tipItemRenderer(): Renderer<T, TipOptionViewHolder> =
    object : Renderer<T, TipOptionViewHolder> {
        override val type: Class<T>
            get() = T::class.java

        override fun createViewHolder(parent: ViewGroup): TipOptionViewHolder {
            return TipOptionViewHolder.create(parent)
        }

        override fun bindViewHolder(
            holder: TipOptionViewHolder,
            item: T
        ) {
            holder.bind(item)
        }
    }

private class TipOptionViewHolder(
    private val binding: ItemTipOptionBinding
) : RecyclerView.ViewHolder(
    binding.root
) {

    companion object {
        fun create(parent: ViewGroup): TipOptionViewHolder {
            return TipOptionViewHolder(
                ItemTipOptionBinding.inflate(
                    parent.layoutInflater(),
                    parent,
                    false
                )
            )
        }
    }

    private var item: TipOptionUiModel? = null

    init {
        binding.root.clicks {
            item?.let { it.onClick(it) }
        }
    }

    fun bind(item: TipOptionUiModel) {
        this.item = item
        binding.root.isActivated = item.isSelected
        binding.root.label = item.label
    }
}