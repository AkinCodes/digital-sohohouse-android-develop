package com.sohohouse.seven.perks.details.adapter

import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem.DiscoverPerks.PerksItem
import com.sohohouse.seven.network.core.models.Body
import java.util.*

sealed class PerksDetailItem(val itemType: Int) {
    companion object {
        const val ITEM_TYPE_HEADER_IMAGE = 0
        const val ITEM_TYPE_HEADER = 1
        const val ITEM_TYPE_BODY = 2
        const val ITEM_TYPE_MORE_PERKS = 3
    }
}

data class PerksDetailHeaderImage(val imageUrl: String) : PerksDetailItem(ITEM_TYPE_HEADER_IMAGE)

data class PerksDetailHeader(
    override val id: String,
    override val city: String?,
    override val category: String?,
    override val onlineOnly: Boolean?,
    override val title: String,
    override val summary: String?,
    override val promotionCode: String?,
    override val imageUrl: String?,
    val headerLine: String?,
    val expiresOn: Date?,
    val datePlaceholder: Int = -1
) : PerksDetailItem(ITEM_TYPE_HEADER), PerkItem

data class PerksDetailBody(val bodyItem: Body) : PerksDetailItem(ITEM_TYPE_BODY)

data class MorePerks(val items: List<PerksItem>) : PerksDetailItem(ITEM_TYPE_MORE_PERKS)