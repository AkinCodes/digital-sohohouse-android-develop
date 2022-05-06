package com.sohohouse.seven.authentication

import java.util.*

enum class NotificationType(val id: String) {
    MESSAGE("message"),
    NEW_MESSAGE_REQUEST("new_message_request"),
    EVENT("eventdetails"), HOME("home"), GENERIC(""), ERROR("error"), NOT_NOTIFICATION("n_n");

    companion object {
        fun fromId(id: String?): NotificationType? {
            if (id.isNullOrEmpty()) return NOT_NOTIFICATION
            return values().firstOrNull { it.id == id.toLowerCase(Locale.getDefault()) }
        }
    }
}
