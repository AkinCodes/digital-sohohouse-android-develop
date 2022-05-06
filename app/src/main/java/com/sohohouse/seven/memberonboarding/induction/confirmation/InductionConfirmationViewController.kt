package com.sohohouse.seven.memberonboarding.induction.confirmation

import com.sohohouse.seven.base.mvpimplementation.ViewController
import com.sohohouse.seven.network.core.models.Venue

interface InductionConfirmationViewController : ViewController {
    fun displayFollowUp(
        houseName: String,
        houseColor: String,
        houseImage: String,
        locationString: String
    )

    fun displayAppointment(
        houseName: String,
        houseColor: String,
        houseImage: String,
        dateString: String,
        locationString: String,
        isOffSite: Boolean
    )

    fun styleForIndependence(isIndependent: Boolean)
    fun openLocationInMaps(uriString: String)
    fun navigateToNextOnboarding()
    fun navigateToInductionBooking(bookingID: String)
    fun setUpLayout(venue: Venue)
}