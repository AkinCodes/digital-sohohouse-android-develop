package com.sohohouse.seven.memberonboarding

import android.content.Intent
import com.sohohouse.seven.authentication.AuthenticationFlowManager
import com.sohohouse.seven.base.BaseActivity
import com.sohohouse.seven.book.eventdetails.bookingsuccess.BookingSuccessActivity
import com.sohohouse.seven.book.eventdetails.bookingsuccess.BookingSuccessItem
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.views.UserBookingState
import com.sohohouse.seven.main.MainActivity
import com.sohohouse.seven.memberonboarding.induction.booking.InductionBookingActivity
import com.sohohouse.seven.memberonboarding.induction.confirmation.InductionConfirmationActivity
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.Venue
import java.util.*

class MemberOnboardingFlowManager(
    val userManager: UserManager,
    private val authFlowManager: AuthenticationFlowManager
) {
    fun navigateBackToInductionBookingActivity(activity: BaseActivity, bookingID: String): Intent {
        return InductionBookingActivity.getIntent(activity, bookingID)
    }

    fun navigateToAppointmentSuccess(
        activity: BaseActivity, eventId: String, eventName: String, eventDate: Date?,
        timeZone: String?, eventImageUrl: String?, houseName: String,
        houseColor: String
    ): Intent {
        val intent = Intent(activity, BookingSuccessActivity::class.java)
        val item = BookingSuccessItem(
            UserBookingState.GUEST_LIST, eventDate, timeZone,
            eventId, eventName, eventImageUrl, houseName, houseColor, isInduction = true
        )
        intent.putExtra(BookingSuccessActivity.BOOKING_SUCCESS_ITEM, item)
        return intent
    }

    fun navigateToFollowUpSuccess(
        activity: BaseActivity, eventId: String, eventName: String, eventImageUrl: String?,
        houseName: String, houseColor: String
    ): Intent {
        val intent = Intent(activity, BookingSuccessActivity::class.java)
        val item = BookingSuccessItem(
            UserBookingState.WAIT_LIST, eventId = eventId,
            eventName = eventName, eventImageUrl = eventImageUrl,
            venueName = houseName, venueColor = houseColor, isInduction = true
        )
        intent.putExtra(BookingSuccessActivity.BOOKING_SUCCESS_ITEM, item)
        return intent
    }

    fun navigateAfterAppointmentSuccess(
        activity: BaseActivity,
        selectedEvent: Event,
        venue: Venue,
        bookingID: String
    ): Intent {
        return InductionConfirmationActivity.getAppointmentIntent(
            activity,
            selectedEvent,
            venue,
            bookingID,
            false
        )
    }

    fun navigateAfterFollowUpSuccess(activity: BaseActivity, localHouse: Venue): Intent {
        return InductionConfirmationActivity.getFollowUpIntent(activity, localHouse)
    }

    fun navigateAfterConfirmation(activity: BaseActivity) {
        userManager.isInducted = true
        val intent = Intent(activity, MainActivity::class.java)
        activity.startActivity(intent)
        activity.finish()
    }

    /*
        fun navigateAfterHouseRules(activity: BaseActivity): Intent {
            val intent = SyncCalendarActivity.getIntentForActivityWithoutToolbar(activity)
            return intent
        }
    */

    fun navigateCompleteMemberOnboarding(activity: BaseActivity) {
        userManager.isInducted = true
        val intent = authFlowManager.navigateFrom(activity)
        activity.startActivity(intent)
        activity.finish()
    }
}