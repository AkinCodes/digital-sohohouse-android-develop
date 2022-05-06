package com.sohohouse.seven.memberonboarding.induction.booking

import androidx.annotation.StringRes
import com.sohohouse.seven.base.error.ErrorDialogViewController
import com.sohohouse.seven.base.error.ErrorViewStateViewController
import com.sohohouse.seven.base.load.LoadViewController
import com.sohohouse.seven.base.mvpimplementation.ViewController
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.Venue
import java.util.*

interface InductionBookingViewController : ViewController, LoadViewController,
    ErrorViewStateViewController, ErrorDialogViewController {
    fun setData(dataList: List<BaseInductItem>)
    fun updateSelectedDate(eventId: String)
    fun updateBookButtonText(@StringRes resID: Int, isEnabled: Boolean)
    fun showAppointmentSuccessModal(
        eventId: String,
        eventDate: Date?,
        timeZone: String?,
        imageURL: String?,
        houseName: String,
        houseColor: String
    )

    fun showFollowupSuccessModal(
        eventId: String,
        imageURL: String?,
        houseName: String,
        houseColor: String
    )

    fun navigateAfterAppointmentSuccess(selectedEvent: Event, venue: Venue, bookingID: String)
    fun navigateAfterFollowUpSuccess(localHouse: Venue)
    fun showBookingError()
}