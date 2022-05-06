package com.sohohouse.seven.book.table.model

import com.sohohouse.seven.book.table.TableBookingHouseDetailsItem
import com.sohohouse.seven.common.extensions.buildAddress
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.network.core.models.Phone
import com.sohohouse.seven.network.core.models.SlotLock
import com.sohohouse.seven.network.core.models.TableReservation
import com.sohohouse.seven.network.core.models.Venue
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import java.io.Serializable
import java.util.*

data class BookedTable(
    val id: String,
    val name: String,
    val address: String,
    val country: String,
    val imageUrl: String,
    val specialNotes: String,
    val phone: Phone,
    var bookedTableDate: BookedTableDate,
    val seats: Int,
    var specialComment: String,
    val restaurantId: String,
    val venueId: String,
    val venueDetails: TableBookingHouseDetailsItem,
    var confirmationNumber: Int = 0
) : Serializable {
    var slotLock: SlotLock? = null

    companion object {
        fun from(
            it: TableReservation,
            parentVenue: Venue?,
            stringProvider: StringProvider
        ): BookedTable {
            return BookedTable(
                id = it.id,
                name = it.venue?.restaurant?.bookingPartnerName ?: "",
                address = it.venue?.buildAddress(true) ?: "",
                country = it.venue?.country ?: "",
                imageUrl = it.restaurant?.getImage() ?: "",
                specialNotes = it.restaurant?.specialNotes ?: "",
                phone = it.phone ?: Phone(),
                bookedTableDate = BookedTableDate(
                    date = it.dateTime,
                    dateTime = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm")
                        .withZone(
                            DateTimeZone.forTimeZone(
                                TimeZone.getTimeZone(
                                    it.venue?.timeZone ?: ""
                                )
                            )
                        )
                        .parseDateTime(it.date_time),
                    it.venue?.timeZone ?: ""
                ),
                seats = it.party_size,
                specialComment = it.special_request ?: "",
                restaurantId = it.restaurant?.id ?: "",
                venueId = it.venue?.id ?: "",
                TableBookingHouseDetailsItem.from(it.venue, parentVenue, stringProvider),
                confirmationNumber = it.confirmation_number
            )
        }
    }
}