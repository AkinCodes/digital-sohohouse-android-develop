package com.sohohouse.seven.guests

import com.sohohouse.seven.common.utils.ZipRequestsUtil
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.models.GuestList
import com.sohohouse.seven.network.core.models.Invite
import com.sohohouse.seven.network.core.models.InviteUpdate
import com.sohohouse.seven.network.core.request.*
import com.sohohouse.seven.network.utils.getApiFormattedDate
import org.threeten.bp.LocalDateTime
import java.text.SimpleDateFormat
import java.util.*

interface GuestListRepository {
    fun getGuestLists(): Either<ServerError, List<GuestList>>

    fun getGuestList(id: String): Either<ServerError, GuestList>

    fun createGuestList(
        name: String,
        venueId: String,
        date: Date,
        notes: String?
    ): Either<ServerError, GuestList>

    fun addGuestToGuestList(guestListId: String, guestName: String): Either<ServerError, Invite>

    fun editGuestName(inviteId: String, guestName: String): Either<ServerError, Invite>

    fun deleteGuestList(guestListId: String): Either<ServerError, Void>
}

class GuestListRepositoryImpl(private val zipRequestsUtil: ZipRequestsUtil) : GuestListRepository {
    override fun getGuestLists(): Either<ServerError, List<GuestList>> {
        return zipRequestsUtil.issueApiCall(
            GetGuestListsRequest(
                LocalDateTime.now().getApiFormattedDate()
            )
        )
    }

    override fun getGuestList(id: String): Either<ServerError, GuestList> {
        return zipRequestsUtil.issueApiCall(GetGuestListRequest(id))
    }


    override fun createGuestList(
        name: String,
        venueId: String,
        date: Date,
        notes: String?
    ): Either<ServerError, GuestList> {
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(date)
        return zipRequestsUtil.issueApiCall(PostGuestListRequest(name, venueId, dateString, notes))
    }

    override fun addGuestToGuestList(
        guestListId: String,
        guestName: String
    ): Either<ServerError, Invite> {
        return zipRequestsUtil.issueApiCall(PostInviteRequest(guestListId, guestName))
    }

    override fun editGuestName(inviteId: String, guestName: String): Either<ServerError, Invite> {
        val model = InviteUpdate(guestName).apply { id = inviteId }
        return zipRequestsUtil.issueApiCall(PatchInviteRequest(model))
    }

    override fun deleteGuestList(guestListId: String): Either<ServerError, Void> {
        return zipRequestsUtil.issueApiCall(DeleteGuestListRequest(guestListId))
    }
}
