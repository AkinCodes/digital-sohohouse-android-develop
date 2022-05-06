package com.sohohouse.seven.guests

import com.sohohouse.seven.network.core.models.GuestList
import com.sohohouse.seven.network.core.models.Invite
import com.sohohouse.seven.network.core.models.JSON_API_TYPE_GUEST_LISTS
import com.sohohouse.seven.network.core.models.Venue
import io.mockk.every
import io.mockk.mockk
import moe.banana.jsonapi2.HasOne
import java.util.*

object GuestsTestHelper {
    fun mockGuestList(
        id: String = "id",
        maxGuests: Int = 1,
        invites: List<Invite> = mutableListOf(),
        date: Date? = Date()
    ): GuestList {
        return mockk<GuestList>().apply {
            every { getId() } returns id
            every { name } returns "My Guest List"
            every { notes } returns "Note"
            every { venue } returns Venue().apply { this.id = "venueId" }
            every { this@apply.invites } returns invites
            every { this@apply.date } returns date
            every { this@apply.maxGuests } returns maxGuests
        }
    }

    fun mockInvite(
        id: String = "id",
        guestListId: String = "guestListId",
        name: String = "John",
        status: String = "PENDING"
    ): Invite {
        return Invite(
            name,
            status,
            HasOne(JSON_API_TYPE_GUEST_LISTS, guestListId)
        ).apply { this.id = id }
    }
}