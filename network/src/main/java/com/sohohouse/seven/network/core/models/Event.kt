package com.sohohouse.seven.network.core.models

import com.squareup.moshi.Json
import moe.banana.jsonapi2.HasMany
import moe.banana.jsonapi2.HasOne
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource

import java.io.Serializable
import java.util.*

@JsonApi(type = "events")
data class Event(
    @field:Json(name = "name") private var _name: String? = "",
    @field:Json(name = "starts_at") var startsAt: Date? = Date(),
    @field:Json(name = "bookable") var bookable: String? = "",
    @field:Json(name = "ends_at") var endsAt: Date? = Date(),
    @field:Json(name = "description") private var _description: String? = "",
    @field:Json(name = "price_cents") var priceCents: Int = 0,
    @field:Json(name = "price_currency") var priceCurrency: String? = "",
    @field:Json(name = "has_waiting_list") private var _hasWaitingList: Boolean? = false,
    @field:Json(name = "tickets") var tickets: Tickets? = null,
    @field:Json(name = "open_for_booking_at") var openForBookingAt: Date? = null,
    @field:Json(name = "max_guests_per_booking") var maxGuestsPerBooking: Int = 0,
    @field:Json(name = "state") var state: String? = "",
    @field:Json(name = "featured") var featured: Boolean? = false,
    @field:Json(name = "images") var images: Images? = null,
    @field:Json(name = "postponed") private var _postponed: Boolean? = false,
    @field:Json(name = "address") private var _address: String? = "",
    @field:Json(name = "is_offsite") private var _isOffsite: Boolean? = false,
    @field:Json(name = "has_lottery") private var _hasLottery: Boolean? = false,
    @field:Json(name = "lottery_drawn") private var _lotteryDrawn: Boolean? = false,
    @field:Json(name = "draw_lottery_at") var drawLotteryAt: Date? = Date(),
    @field:Json(name = "category") var category: String? = "",
    @field:Json(name = "links") var links: List<Link>? = null,
    @field:Json(name = "is_ticketless") private var _isTicketless: Boolean? = false,
    @field:Json(name = "is_non_refundable") private var _isNonRefundable: Boolean? = false,
    @field:Json(name = "resource.resource_meta") var resourceResourceMeta: HasOne<EventInstructor>? = null,
    @field:Json(name = "booking") var booking: HasOne<EventBooking>? = null,
    @field:Json(name = "venue") var venue: HasOne<Venue>? = HasOne(),
    @field:Json(name = "resource") var resource: HasOne<EventResource> = HasOne(),
    @field:Json(name = "film") var film: HasOne<EventsFilm>? = HasOne(),
    @field:Json(name = "translations") var translations: HasMany<EventsTranslation>? = HasMany(),
    @field:Json(name = "send_booking_confirmation_at") var sendBookingConfirmationAt: Date? = null,
    @field:Json(name = "cancel_unconfirmed_bookings_at") var cancelUnconfirmedBookingsAt: Date? = null,
    @field:Json(name = "cancellable_until") var cancellableUntil: Date? = null,
    @field:Json(name = "digital") var digitalInfo: DigitalInfo? = null,
) : Resource(), Serializable {
    val name: String
        get() = _name ?: ""
    val description: String
        get() = _description ?: ""
    val hasWaitingList: Boolean
        get() = _hasWaitingList ?: false
    val postponed: Boolean
        get() = _postponed ?: false
    val address: String
        get() = _address ?: ""
    val isOffsite: Boolean
        get() = _isOffsite ?: false
    val hasLottery: Boolean
        get() = _hasLottery ?: false
    val lotteryDrawn: Boolean
        get() = _lotteryDrawn ?: false
    val isTicketless: Boolean
        get() = _isTicketless ?: false
    val isNonRefundable: Boolean
        get() = _isNonRefundable ?: false
    val bookingState get() = booking?.get(document)?.state

    val eventType: String
        get() = try {
            resource.get(document).resourceType
        } catch (e: Exception) {
            ""
        }

    fun isFree(): Boolean = priceCents == 0
}

data class Tickets(
    @field:Json(name = "available") var available: Int = 0,
    @field:Json(name = "total") var total: Int = 0,
) : Serializable

data class Images(
    @field:Json(name = "small") var small: String? = "",
    @field:Json(name = "medium") var medium: String? = "",
    @field:Json(name = "large") var large: String? = "",
    @field:Json(name = "xlarge") var xlarge: String? = "",
) : Serializable

data class Link(
    @field:Json(name = "description") var description: String? = "",
    @field:Json(name = "url") var url: String? = "",
) : Serializable

