package com.sohohouse.seven.home.perks

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.databinding.DiscoverPerksCarouselBinding
import com.sohohouse.seven.databinding.ItemHomeContentSectionHeaderBinding

const val DISCOVER_PERKS_LAYOUT = R.layout.discover_perks_carousel

class DiscoverPerksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = DiscoverPerksCarouselBinding.bind(itemView)
    private val headerBinding = ItemHomeContentSectionHeaderBinding.bind(binding.root)
    private val perksAdapter = DiscoverPerksAdapter()

    init {
        with(binding.recyclerView) {
            layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = perksAdapter
        }
    }

    fun bind(
        item: BaseAdapterItem.DiscoverPerks,
        onSeeAllClick: () -> Unit,
        onPerkClicked: (id: String, title: String?, promoCode: String?) -> Unit
    ) {
        perksAdapter.let {
            it.submitList(item.perks)
            it.onPerkClicked = onPerkClicked
        }
        headerBinding.seeAll.setOnClickListener { onSeeAllClick() }
    }

}