package com.sohohouse.seven.perks.details

import com.sohohouse.seven.housenotes.detail.HouseNoteDetailItemType
import com.sohohouse.seven.housenotes.detail.model.HouseNoteDetailBaseItem
import com.sohohouse.seven.network.core.models.Perk

class PerkDetailClipboardItem constructor(
    val id: String,
    val name: String,
    val promoCode: String,
    val url: String,
    val benefitType: String
) : HouseNoteDetailBaseItem(HouseNoteDetailItemType.FOOTER_CLIPBOARD) {

    constructor(perks: Perk) : this(
        perks.id,
        perks.title ?: "",
        perks.promotionCode ?: "",
        perks.perkUrl ?: "",
        perks.benefitType ?: ""
    )

}
