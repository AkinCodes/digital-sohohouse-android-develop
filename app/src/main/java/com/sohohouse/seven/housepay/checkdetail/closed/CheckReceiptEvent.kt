package com.sohohouse.seven.housepay.checkdetail.closed

sealed interface CheckReceiptEvent {
    data class OpenLink(val url: String) : CheckReceiptEvent
    data class CallPhone(val phoneNumber: String) : CheckReceiptEvent
}