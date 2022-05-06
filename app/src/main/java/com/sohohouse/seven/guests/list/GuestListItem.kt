package com.sohohouse.seven.guests.list

import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.common.adapterhelpers.StickyHeaderAdapter
import com.sohohouse.seven.common.extensions.isNextWeek
import com.sohohouse.seven.common.extensions.isThisWeek
import com.sohohouse.seven.common.extensions.isToday
import com.sohohouse.seven.common.viewholders.DateHeaderViewHolder
import com.sohohouse.seven.guests.InviteStatus
import java.util.*

sealed class GuestListItem : DiffItem {

    open val id: String = ""

    open fun getHeaderId(): Int = StickyHeaderAdapter.NO_ID


    object DescriptionItem : GuestListItem() {
        override val key: Any?
            get() = HEADER_ID
    }

    object NewInvitationItem : GuestListItem() {
        override val key: Any?
            get() = NEW_INVITATION_ID
    }

    object ListHeaderItem : GuestListItem() {
        override val key: Any?
            get() = MY_INVITATIONS_ID
    }

    data class GuestInvitationItem(
        override val id: String,
        val title: String,
        val location: String?,
        val address: String?,
        val date: Date?,
        val imageUrl: String?,
        val status: InviteStatus? = null
    ) : GuestListItem() {
        override val key: Any?
            get() = id

        override fun getHeaderId(): Int {
            return when {
                date?.isToday() == true -> DateHeaderViewHolder.HEADER_TYPE_TODAY
                date?.isThisWeek() == true -> DateHeaderViewHolder.HEADER_TYPE_THIS_WEEK
                date?.isNextWeek() == true -> DateHeaderViewHolder.HEADER_TYPE_NEXT_WEEK
                date != null -> DateHeaderViewHolder.HEADER_TYPE_IN_FUTURE
                else -> StickyHeaderAdapter.NO_ID
            }
        }
    }

    companion object {
        private const val HEADER_ID = "guest_list_header"
        private const val NEW_INVITATION_ID = "new_invitation"
        private const val MY_INVITATIONS_ID = "my_invitations"
    }
}
