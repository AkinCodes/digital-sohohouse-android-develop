package com.sohohouse.seven.common.views.inappnotification

class InAppNotificationAdapterItem(
    val imageDrawableId: Int = -1,
    val imageUrl: String? = "",
    val status: String = "",
    val eventDate: String = "",
    val eventTitle: String = "",
    val eventHouse: String = "",
    val textBody: String = "",
    val primaryButtonString: String,
    val secondaryButtonString: String = "",
    val isTextBodyVisible: Boolean = false,
    val isSecondaryButtonVisible: Boolean = true,
    val primaryClicked: (() -> Unit)? = null,
    val secondaryClicked: (() -> Unit)? = null
)