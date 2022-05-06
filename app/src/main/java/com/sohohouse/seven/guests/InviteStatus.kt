package com.sohohouse.seven.guests

import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import com.sohohouse.seven.R

enum class InviteStatus(@AttrRes val color: Int, @StringRes val stringRes: Int) {
    PENDING(R.attr.colorInvitationPending, R.string.invite_status_pending),
    ACCEPTED(R.attr.colorInvitationAccepted, R.string.invite_status_accepted),
    REJECTED(R.attr.colorInvitationRejected, R.string.invite_status_rejected),
    CANCELLED(R.attr.colorInvitationCancelled, R.string.invite_status_cancelled),
    REMOVED(R.attr.colorInvitationRemoved, R.string.invite_status_removed)
}