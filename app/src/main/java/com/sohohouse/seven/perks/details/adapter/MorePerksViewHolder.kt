package com.sohohouse.seven.perks.details.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import com.sohohouse.seven.base.GenericAdapter.ViewHolder
import com.sohohouse.seven.common.extensions.context
import com.sohohouse.seven.databinding.ViewHolderPerksDetailMorePerksBinding
import com.sohohouse.seven.home.list.PerksAdapterListener
import com.sohohouse.seven.home.perks.DiscoverPerksAdapter
import com.sohohouse.seven.perks.details.PerksDetailActivity

class MorePerksViewHolder(binding: ViewHolderPerksDetailMorePerksBinding) :
    ViewHolder<MorePerks>(binding.root), PerksAdapterListener {

    private val adapter = DiscoverPerksAdapter()

    init {
        with(binding.recyclerView) {
            layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = this@MorePerksViewHolder.adapter
        }
    }

    override fun bind(item: MorePerks) {
        adapter.onPerkClicked = ::onPerkClicked
        adapter.submitList(item.items)
    }

    override fun onPerkClicked(id: String, title: String?, promoCode: String?) {
        PerksDetailActivity.start(context, id)
    }

}