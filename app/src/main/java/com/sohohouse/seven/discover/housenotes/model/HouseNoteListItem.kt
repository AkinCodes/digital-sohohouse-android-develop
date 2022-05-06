package com.sohohouse.seven.discover.housenotes.model

import com.sohohouse.seven.common.design.list.ListItem

class HouseNoteListItem(
    override val id: String,
    override val title: String,
    override val subtitle: String? = null,
    override val label: String? = null,
    override val imageUrl: String?
) : ListItem, HouseNoteItem