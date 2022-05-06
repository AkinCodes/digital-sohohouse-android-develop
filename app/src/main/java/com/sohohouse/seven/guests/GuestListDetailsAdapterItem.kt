package com.sohohouse.seven.guests

import androidx.annotation.StringRes
import com.sohohouse.seven.base.DiffItem

sealed class GuestListDetailsAdapterItem : DiffItem {
    data class GuestListFormItem(
        val houseItem: GuestListFormHouseItem,
        val dateItem: GuestListFormDateItem,
        val note: String?,
        val isNoteEnabled: Boolean
    ) : GuestListDetailsAdapterItem() {
        override val key: Any?
            get() = GuestListFormItem::class.java
    }

    data class GuestItem(
        val inviteId: String,
        val guestName: String,
        val status: InviteStatus?,
        val showStatus: Boolean
    ) : GuestListDetailsAdapterItem() {
        override val key: Any?
            get() = inviteId
    }

    data class FormHeaderItem(@StringRes val label: Int, val expanded: Boolean) :
        GuestListDetailsAdapterItem() {
        override val key: Any?
            get() = FormHeaderItem::class.java
    }

    data class GuestsHeaderItem(@StringRes val text: Int) : GuestListDetailsAdapterItem() {
        override val key: Any?
            get() = GuestsHeaderItem::class.java
    }

    data class GuestsSubheaderItem(@StringRes val text: Int) : GuestListDetailsAdapterItem() {
        override val key: Any?
            get() = GuestsSubheaderItem::class.java
    }

    data class AddGuestItem(@StringRes val cta: Int) : GuestListDetailsAdapterItem() {
        override val key: Any?
            get() = AddGuestItem::class.java
    }
}