package com.sohohouse.seven.housenotes.detail.model

import com.sohohouse.seven.housenotes.detail.HouseNoteDetailItemType
import java.util.*

open class HouseNoteDetailHeaderCardItem(
    val houseName: String,
    val title: String, val headerLine: String, val authorName: String,
    val publishDate: Date?,
    val datePlaceholder: Int = -1
) :
    HouseNoteDetailBaseItem(HouseNoteDetailItemType.HEADER_CARD)