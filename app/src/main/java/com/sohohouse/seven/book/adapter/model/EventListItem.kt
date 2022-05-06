package com.sohohouse.seven.book.adapter.model

import com.sohohouse.seven.common.design.list.ListItem
import com.sohohouse.seven.network.core.models.Event

class EventListItem constructor(
    override val id: String,
    override val title: String?,
    override val subtitle: String?,
    override val label: String?,
    override val imageUrl: String?
) : ListItem {

    constructor(event: Event) : this(
        id = event.id,
        title = event.name,
        subtitle = event.address,
        null,
        imageUrl = event.images?.large
    )
}