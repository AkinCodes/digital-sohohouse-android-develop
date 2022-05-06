package com.sohohouse.seven.discover.housenotes.model

import com.sohohouse.seven.common.design.card.XLargeCard

class HouseNoteCard(
    override val id: String,
    override val title: String,
    override val imageUrl: String?,
    val videoUrl: String?
) : XLargeCard, HouseNoteItem