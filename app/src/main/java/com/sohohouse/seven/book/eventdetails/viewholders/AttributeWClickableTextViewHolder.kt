package com.sohohouse.seven.book.eventdetails.viewholders

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.utils.CurrencyUtils
import com.sohohouse.seven.common.utils.DateUtils
import com.sohohouse.seven.common.views.EventType
import com.sohohouse.seven.common.views.eventdetaillist.*
import com.sohohouse.seven.network.core.models.Period
import com.sohohouse.seven.databinding.EventDetailsAttributeLayoutBinding
import java.util.*

class AttributeWClickableTextViewHolder(
    private val binding: EventDetailsAttributeLayoutBinding
) : RecyclerView.ViewHolder(binding.root), BaseEventAttributesViewHolder {

    override val label: TextView
        get() = binding.label

    override val description: TextView
        get() = binding.description

    fun bind(item: EventAttributeAdapterItem) {

        item.labelStringRes?.let { setLabel(it) }
        item.CTAStringRes?.let { setCTA(it, item.CTAListener) }

        when (item) {
            is EventTicketsAdapterItem -> onEventTicketItem(item)
            is AddressAdapterItem -> onAddressItem(item)
            is EventGuestAdapterItem -> onEventGuestItem(item)
            is EventDateAdapterItem -> onEventDateItem(item)
            is HouseDetailsAdapterItem -> onHouseDetailsItem(item)
            else -> item.description?.let { setDescription(it) }
        }
    }

    private fun onHouseDetailsItem(item: HouseDetailsAdapterItem) {

        item.periods?.let {
            val periods = getGroupedPeriods(
                it
            )
            binding.openCloseTime.apply {
                setVisible()
                text = periods.first
            }
            binding.weekdays.apply {
                setVisible()
                text = periods.second
            }
        }

        item.phoneNumber?.let {
            binding.phoneNumber.apply {
                setVisible()
                text = it
            }
        }
        item.description?.let { setSupportingText(it) }
        item.venueName?.let { setDescription(it) }
        item.isOffsite?.let { setIsOffsite(it) }
    }

    private fun onEventTicketItem(item: EventTicketsAdapterItem) {
        setDescription(getDescriptionText(item))
        setBookingStartTime(item)
        setSupportingText(getSupportingText(item))
    }

    private fun getDescriptionText(item: EventTicketsAdapterItem): String {
        val eventTypeString = getString(item.eventType.label).toLowerCase(Locale.US)

        return if (item.guestsAllowed > 0) {
            val description = when {
                item.isTicketless -> getQuantityString(
                    R.plurals.explore_events_guests_welcome_ticketless,
                    item.guestsAllowed
                )
                else -> getQuantityString(
                    R.plurals.explore_events_guests_welcome,
                    item.guestsAllowed
                )
            }
            description.replaceBraces(
                item.guestsAllowed.toWords(this.context).lowercase(),
                eventTypeString
            )
        } else {
            val description = when {
                item.isTicketless -> getString(R.string.explore_events_member_welcome_ticketless)
                else -> getString(R.string.explore_events_member_welcome)
            }
            description.replaceBraces(eventTypeString)
        }
    }

    private fun setBookingStartTime(item: EventTicketsAdapterItem) {
        item.bookingsOpensAt?.let {
            binding.eventBookingOpeningTime.setVisible()
            binding.eventBookingOpeningTime.text = getString(
                R.string.explore_events_event_booking_opens_at,
                it.getFormattedDateTime(item.timeZone)
            )
        }
    }

    private fun getSupportingText(item: EventTicketsAdapterItem): String {
        val builder = StringBuilder()
        builder.append(
            when {
                item.lotteryDate != null -> {
                    when {
                        item.isLotteryDrawn -> {
                            getString(R.string.explore_events_event_tickets_lottery_drawn_label)
                                .replaceBraces(item.lotteryDate.getFormattedDateTime(item.timeZone))
                        }
                        item.isLotteryOpen -> {
                            getString(R.string.explore_events_event_tickets_lottery_draw_upcoming_label)
                                .replaceBraces(item.lotteryDate.getFormattedDateTime(item.timeZone))
                        }
                        else -> {
                            getString(R.string.explore_events_event_tickets_lottery_draw__not_yet_open_label)
                                .replaceBraces(item.lotteryDate.getFormattedDateTime(item.timeZone))
                        }
                    }
                }
                item.isTicketless -> getString(R.string.explore_events_event_tickets_free_label)
                (item.price > 0 && item.currencyCode.isNotEmpty()) -> {
                    getString(R.string.explore_events_event_tickets_price_label)
                        .replaceBraces(
                            CurrencyUtils.getFormattedPrice(
                                item.price,
                                item.currencyCode
                            )
                        )
                }
                else -> getString(R.string.explore_events_event_tickets_required_free_label)
            }
        )

        if (EventType.CINEMA_EVENT == item.eventType && item.price > 0 && item.currencyCode.isNotEmpty()) {
            builder.append("\n")
            builder.append(
                getString(R.string.tickets_lottery_drawn_screening_paid_label)
                    .replaceBraces(CurrencyUtils.getFormattedPrice(item.price, item.currencyCode))
            )
        }

        return builder.toString()
    }

    private fun onAddressItem(item: AddressAdapterItem) {
        item.description?.let { setDescription(it) }
        item.isOffsite?.let { setIsOffsite(it) }
    }

    private fun onEventGuestItem(item: EventGuestAdapterItem) {
        item.description?.let { setDescription(it) }
        setAlternateLabelText(getLabelText(item))
    }

    @SuppressLint("DefaultLocale")
    private fun getLabelText(item: EventGuestAdapterItem): String? {
        return when {
            item.guestNum > 0 -> getString(R.string.explore_events_event_your_guests_number_label).replaceBraces(
                item.maxGuestNum.toString()
            )
            else -> null
        }
    }

    private fun onEventDateItem(item: EventDateAdapterItem) {
        val startDateString = item.startDate?.getFormattedDateTime(item.timeZone)

        if (startDateString == null) {
            setDescription(startDateString.requiresErrorMessage(binding.root.context))
            return
        }

        ifNotNull(item.startDate, item.endDate) { startData, endDate ->
            if (startData.isSameDay(endDate, item.timeZone)) {
                setDescription(
                    getString(R.string.explore_events_date_time_to_placeholders_label).replaceBraces(
                        startDateString,
                        endDate.getFormattedTime(item.timeZone)
                    )
                )
            } else {
                setDescription(
                    getString(R.string.explore_events_date_time_to_placeholders_label).replaceBraces(
                        startDateString,
                        endDate.getFormattedDateTime(item.timeZone)
                    )
                )
            }
        }
    }

    override fun setDescription(text: String) {
        super.setDescription(text)
        binding.offsiteEvent.setGone()
    }

    private fun setSupportingText(text: String) {
        with(binding.supportingText) {
            this.text = text
            isVisible = text.isNotEmpty()
        }
    }

    private fun setCTA(stringRes: Int, clickListener: (() -> Unit)?) {
        with(binding.CTA) {
            setText(stringRes)
            setOnClickListener { clickListener?.let { onClick -> onClick() } }
            setVisible()
        }
    }

    fun setExtraBottomPadding() {
        binding.extraBottomPadding.setVisible()
    }

    private fun setIsOffsite(isOffsite: Boolean) {
        with(binding.offsiteEvent) {
            if (isOffsite) {
                setVisible()
                text = getString(R.string.explore_events_event_offsite_label)
            } else {
                setGone()
            }
        }
    }

    private fun getGroupedPeriods(
        periods: List<Period>
    ): Pair<String, String> {
        val stringBuilderOpenClose = StringBuilder()
        val stringBuilderWeekDays = StringBuilder()
        val sorted = periods.sortedBy { period ->
            period.venueOpen.day
        }

        val grouped =
            sorted.distinctBy { sortedPeriods -> sortedPeriods.venueOpen.time; sortedPeriods.venueClose.time }

        val list = arrayListOf<Pair<String, String>>()

        grouped.map { period ->

            stringBuilderOpenClose.append(
                getString(
                    R.string.explore_events_house_event_date_time_placeholder,
                    DateUtils.reformatOperatingHoursString(period.venueOpen.time, "h:mm a")
                        .uppercase(),
                    DateUtils.reformatOperatingHoursString(period.venueClose.time, "h:mm a")
                        .uppercase()
                )

            ).append("\n")
            list.add(Pair(period.venueOpen.time, period.venueClose.time))

        }

        list.forEach { times ->

            sorted.let { sorted ->

                val from = sorted.find { from ->
                    times.first == from.venueOpen.time && times.second == from.venueClose.time
                }?.venueOpen?.day?.name
                val till =
                    sorted.findLast { times.first == it.venueOpen.time && times.second == it.venueClose.time }?.venueOpen?.day?.name

                if (from != null && till != null) {
                    if (from == till)
                        stringBuilderWeekDays.append(from.lowerCaseEveryCharExceptFirst())
                            .append("\n")
                    else
                        stringBuilderWeekDays.append(
                            getString(
                                R.string.explore_events_house_event_details_weekday_placeholder,
                                from.lowerCaseEveryCharExceptFirst(),
                                till.lowerCaseEveryCharExceptFirst()
                            )
                        ).append("\n")
                }
            }
        }

        return Pair(stringBuilderOpenClose.toString(), stringBuilderWeekDays.toString())
    }
}