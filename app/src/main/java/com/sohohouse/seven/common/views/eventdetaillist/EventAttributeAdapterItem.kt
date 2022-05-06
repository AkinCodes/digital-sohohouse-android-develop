package com.sohohouse.seven.common.views.eventdetaillist

import androidx.annotation.StringRes
import com.sohohouse.seven.R
import com.sohohouse.seven.book.eventdetails.EventDetailsAdapterItemType
import com.sohohouse.seven.common.extensions.getFormattedDateTime
import com.sohohouse.seven.common.extensions.replaceBraces
import com.sohohouse.seven.common.views.EventType
import com.sohohouse.seven.common.views.JoinEventListener
import com.sohohouse.seven.common.views.RemindMeButtonStatus
import com.sohohouse.seven.common.views.RemindMeListener
import com.sohohouse.seven.network.core.models.Period
import com.sohohouse.seven.network.core.models.Venue
import java.util.*

abstract class EventAttributeAdapterItem(
    @StringRes open val labelStringRes: Int? = null,
    val description: String? = null,
    @StringRes val CTAStringRes: Int? = null,
    val CTAListener: (() -> Unit)? = null,
    adapterItemType: EventDetailsAdapterItemType = EventDetailsAdapterItemType.ATTRIBUTE
) : BaseEventDetailsAdapterItem(adapterItemType)

/** Event Details Attribute Adapter Item **/
class EventTicketsAdapterItem(
    val isTicketless: Boolean,
    val lotteryDate: Date?,
    val timeZone: String,
    val isLotteryDrawn: Boolean,
    val price: Int,
    val currencyCode: String,
    val eventType: EventType,
    val guestsAllowed: Int,
    val isLotteryOpen: Boolean,
    val bookingsOpensAt: Date? = null,
) :
    EventAttributeAdapterItem(labelStringRes = R.string.explore_events_event_tickets_label)

class EventDateAdapterItem constructor(
    val startDate: Date?,
    val endDate: Date?,
    val timeZone: String,
    onAddToCalendarClick: () -> Unit
) :
    EventAttributeAdapterItem(
        labelStringRes = R.string.explore_events_event_date_label,
        description = "",
        CTAStringRes = R.string.explore_events_event_calendar_cta,
        CTAListener = onAddToCalendarClick
    )

open class EventDescriptionAdapterItem(
    @StringRes labelStringRes: Int,
    description: String = "",
    @StringRes val descriptionStringRes: Int? = null
) :
    EventAttributeAdapterItem(
        labelStringRes = labelStringRes,
        description = description,
        adapterItemType = EventDetailsAdapterItemType.DESCRIPTION
    )

class AddressAdapterItem(
    address: String,
    onClickListener: () -> Unit,
    val isOffsite: Boolean? = null
) :
    EventAttributeAdapterItem(
        labelStringRes = R.string.explore_events_event_address_label,
        description = address,
        CTAStringRes = R.string.explore_events_event_map_cta,
        CTAListener = onClickListener
    )

class EventCancellationAdapterItem(
    private val date: Date,
    private val timeZone: String,
    private val isPaid: Boolean,
    private val isNonRefundable: Boolean
) :
    EventDescriptionAdapterItem(
        labelStringRes = R.string.explore_events_event_cancellation_label,
        description = ""
    ) {

    fun getDescription(): Int {
        return when {
            isNonRefundable -> R.string.non_refundable_cancellation_policy
            isPaid -> R.string.explore_events_event_cancellation_supporting
            else -> R.string.explore_events_event_cancellation_free_supporting
        }
    }

    fun replacePlaceHolderWithRealDate(str: String): String {
        if (isNonRefundable) return str
        return str.replaceBraces(date.getFormattedDateTime(timeZone))
    }
}

class EventDepositPolicyAdapterItem(
    val price: Int,
    val currencyCode: String?
) : EventDescriptionAdapterItem(
    labelStringRes = R.string.explore_events_event_deposit_label,
    descriptionStringRes = R.string.event_cancellation_supporting_screening_paid
)

class EventCtaAdapterItem(
    CtaStringRes: Int,
    val listener: JoinEventListener
) : EventAttributeAdapterItem(
    CTAStringRes = CtaStringRes,
    adapterItemType = EventDetailsAdapterItemType.CTA
)

class EventReminderAdapterItem(
    var status: RemindMeButtonStatus,
    val listener: RemindMeListener
) : EventAttributeAdapterItem(adapterItemType = EventDetailsAdapterItemType.REMINDER)

/** Base Event Attribute Adapter Item **/
data class EventGuestAdapterItem(
    val maxGuestNum: Int,
    val guestNum: Int = 0,
    val eventId: String,
    val eventName: String,
    val eventType: String?
) : EventAttributeAdapterItem()

data class EventMembershipAdapterItem(
    val eventId: String,
    val eventName: String,
    val eventType: String?,
    override val labelStringRes: Int,
    val hasLink: Boolean
) : EventAttributeAdapterItem(
    labelStringRes,
    adapterItemType = EventDetailsAdapterItemType.MEMBERSHIP
)

data class HouseDetailsAdapterItem(
    val onClickListener: () -> Unit,
    val periods: List<Period>? = null,
    val venueName: String?,
    val phoneNumber: String?,
    val address: String,
    val isOffsite: Boolean? = null
) : EventAttributeAdapterItem(
    labelStringRes = R.string.explore_events_house_event_details_label, description = address,
    CTAStringRes = R.string.explore_events_event_map_cta,
    CTAListener = onClickListener
) {
    companion object {
        fun getVenueAddress(childVenue: Venue, parentVenue: Venue?): String {
            val venue = if (childVenue.venueAddress.lines.isNullOrEmpty())
                parentVenue ?: childVenue else childVenue

            val address = StringBuilder()

            if (parentVenue?.id != childVenue.id && parentVenue?.name != null)
                address.append("${childVenue.name},\n")

            venue.venueAddress.lines?.map { line ->
                address.append("${line}, ")
            }
            address.append("${venue.city} ${venue.venueAddress.postalCode}, ${venue.venueAddress.country}")
            return address.toString()
        }
    }
}