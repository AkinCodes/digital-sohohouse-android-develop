package com.sohohouse.seven.memberonboarding.induction.booking

import com.sohohouse.seven.common.extensions.getFormattedDateTime
import com.sohohouse.seven.common.extensions.setTimeToMidNight
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.Venue
import java.util.*

class InductionBookingListFactory {
    fun getTimeItemList(
        house: Venue,
        weekItems: List<Event>,
        isIndependent: Boolean
    ): List<BaseInductItem> {
        val firstWeekItems = mutableListOf<Event>()
        val nextWeekItems = mutableListOf<Event>()
        val endCalendar = Calendar.getInstance()
        endCalendar.add(Calendar.DAY_OF_YEAR, 7)
        val endDate = endCalendar.setTimeToMidNight(false)
        for (item in weekItems) {
            item.startsAt?.let {
                if (it.before(endDate)) {
                    firstWeekItems.add(item)
                } else {
                    nextWeekItems.add(item)
                }
            }
        }

        val results = mutableListOf<BaseInductItem>()
        results.add(
            HeaderInductItem(
                house.venueColors.house,
                house.name,
                house.house.get(house.document)?.houseImageSet?.largePng,
                isIndependent
            )
        )
        results.add(SectionWeekInductItem())
        if (firstWeekItems.isEmpty()) {
            results.add(AppointmentsNoneInductItem())
        } else {
            for (item in firstWeekItems) {
                results.add(
                    AppointmentInductItem(
                        item.id,
                        item.startsAt?.getFormattedDateTime(house.timeZone) ?: ""
                    )
                )
            }
        }
        results.add(SectionMoreWeekInductItem())
        if (nextWeekItems.isEmpty()) {
            results.add(AppointmentsNoneInductItem())
        } else {
            for (item in nextWeekItems) {
                results.add(
                    AppointmentInductItem(
                        item.id,
                        item.startsAt?.getFormattedDateTime(house.timeZone) ?: ""
                    )
                )
            }
        }
        results.add(FollowUpInductItem())
        return results
    }

    fun getNoneScheduledList(house: Venue, isIndependent: Boolean): List<BaseInductItem> {
        val results = mutableListOf<BaseInductItem>()
        results.add(
            HeaderInductItem(
                colorString = house.venueColors.house,
                name = house.name,
                imageURL = house.house.get(house.document)?.houseImageSet?.largePng,
                isPlanner = isIndependent
            )
        )
        results.add(NoneScheduledInductItem())
        return results
    }
}