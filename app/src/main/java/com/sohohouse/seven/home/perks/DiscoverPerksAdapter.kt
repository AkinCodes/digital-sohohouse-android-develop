package com.sohohouse.seven.home.perks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.base.BaseRecyclerDiffAdapter
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.extensions.context
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.databinding.ViewDiscoverPerksItemBinding
import com.sohohouse.seven.home.perks.DiscoverPerksAdapter.PerksItemViewHolder

class DiscoverPerksAdapter() :
    BaseRecyclerDiffAdapter<PerksItemViewHolder, BaseAdapterItem.DiscoverPerks.PerksItem>() {

    var onPerkClicked: (id: String, title: String?, promoCode: String?) -> Unit = { _, _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerksItemViewHolder {
        return PerksItemViewHolder(
            ViewDiscoverPerksItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PerksItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PerksItemViewHolder(private val binding: ViewDiscoverPerksItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val item = getItem(adapterPosition)
                onPerkClicked(item.id, item.title, item.promotionCode)
            }
        }

        fun bind(item: BaseAdapterItem.DiscoverPerks.PerksItem) = with(binding) {
            title.text = item.title
            image.setImageFromUrl(item.imageUrl)
            cityTag.text = item.cityTag(context)
            summary.text = item.summary
        }
    }
}
