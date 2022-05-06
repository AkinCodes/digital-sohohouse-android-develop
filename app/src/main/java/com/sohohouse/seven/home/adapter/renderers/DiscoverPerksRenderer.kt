package com.sohohouse.seven.home.adapter.renderers

import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.home.perks.DiscoverPerksViewHolder

class DiscoverPerksRenderer(
    private val onSeeAllPerkButtonClicked: () -> Unit,
    private val onPerkClicked: (id: String, title: String?, promoCode: String?) -> Unit
) : Renderer<BaseAdapterItem.DiscoverPerks, DiscoverPerksViewHolder> {

    override val type: Class<BaseAdapterItem.DiscoverPerks> =
        BaseAdapterItem.DiscoverPerks::class.java

    override fun createViewHolder(parent: ViewGroup): DiscoverPerksViewHolder {
        return DiscoverPerksViewHolder(createItemView(parent, R.layout.discover_perks_carousel))
    }

    override fun bindViewHolder(
        holder: DiscoverPerksViewHolder,
        item: BaseAdapterItem.DiscoverPerks
    ) {
        holder.bind(item, onSeeAllPerkButtonClicked, onPerkClicked)
    }
}