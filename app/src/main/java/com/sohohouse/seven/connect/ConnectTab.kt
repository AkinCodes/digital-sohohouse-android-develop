package com.sohohouse.seven.connect

import androidx.annotation.StringRes
import com.sohohouse.seven.R

enum class ConnectTab(@StringRes val resId: Int, val id: Int) {
    NOTICEBOARD(R.string.label_noticeboard, 1),
    MESSAGES(R.string.label_messages, 2),
    CONNECTION_REQUESTS(R.string.my_connections_requests, 3),
    MY_CONNECTIONS(R.string.account_my_contacts, 4)
}