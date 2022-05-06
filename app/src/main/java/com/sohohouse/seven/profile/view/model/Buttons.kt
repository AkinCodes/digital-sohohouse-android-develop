package com.sohohouse.seven.profile.view.model

import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.profile.*

data class Buttons(
    val list: List<Button>
) : DiffItem {

    override val key: Any
        get() = list

    companion object {
        fun from(status: MutualConnectionStatus?): Buttons? {
            return when (status) {
                NotAvailableMySelf -> Buttons(listOf(ShareProfileButton, EditButton))
                Blocked -> Buttons(listOf(UnblockButton))
                NotConnected -> Buttons(listOf(ConnectButton, MessageButton, OptionsMenu))
                RequestReceived -> Buttons(listOf(IgnoreButton, AcceptButton, OptionsMenu))
                RequestSent -> Buttons(listOf(RequestSentButton, MessageButton, OptionsMenu))
                Connected -> Buttons(listOf(MessageButton, OptionsMenu))
                NotAvailable -> null
                else -> null
            }
        }
    }
}
