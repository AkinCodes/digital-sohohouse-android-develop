package com.sohohouse.seven.more.bookings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.getFormattedDateTime
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.network.core.models.EventBooking
import javax.inject.Inject

class PastBookingDetailViewModel @Inject constructor(
    private val venueRepo: VenueRepo,
    private val stringProvider: StringProvider,
    private val userManager: UserManager,
    analyticsManager: AnalyticsManager
) : BaseViewModel(analyticsManager) {

    private val _data = MutableLiveData<BookingData>()

    val data: LiveData<BookingData> get() = _data

    fun init(eventBooking: EventBooking) {
        val venueId = eventBooking.venue.id
        val venue = venueRepo.venues().findById(venueId)
        val event = eventBooking.event

        val dateAndTime = eventBooking.event?.startsAt?.getFormattedDateTime(venue?.timeZone) ?: ""

        val location = venue?.name ?: ""

        val imageUrl = event?.images?.large ?: ""

        val thankYouMsg =
            stringProvider.getString(R.string.thanks_for_coming, userManager.profileFirstName)

        val supportingMsg =
            "${stringProvider.getString(R.string.we_hope_you_enjoyed)}\n${event?.name ?: ""}"

        _data.value = BookingData(imageUrl, thankYouMsg, supportingMsg, dateAndTime, location)
    }

    override fun onScreenViewed() {
        setScreenNameInternal(AnalyticsManager.Screens.PastBookingsDetail.name)
    }

    data class BookingData(
        val imageUrl: String,
        val thankYouMsg: String,
        val supportingMsg: String,
        val dateAndTime: String,
        val location: String
    )
}