package com.sohohouse.seven.home.suggested_people

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sohohouse.seven.common.design.adapter.Renderer
import com.sohohouse.seven.databinding.SuggestedPeopleBinding

class SuggestedPeopleRenderer(
    private val recyclerItemCallback: (userID: String) -> Unit,
    private val seeAllCallback: (View) -> Unit,
    private val optInCallback: (View) -> Unit,
) : Renderer<SuggestedAdapterItem, SuggestedCarouselViewHolder> {
    override val type: Class<SuggestedAdapterItem> = SuggestedAdapterItem::class.java

    override fun createViewHolder(parent: ViewGroup): SuggestedCarouselViewHolder {
        return SuggestedCarouselViewHolder(
            SuggestedPeopleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun bindViewHolder(holder: SuggestedCarouselViewHolder, item: SuggestedAdapterItem) {
        holder.bind(item, recyclerItemCallback, seeAllCallback, optInCallback)
    }

}
