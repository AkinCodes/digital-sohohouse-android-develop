package com.sohohouse.seven.memberonboarding.induction.confirmation

import com.sohohouse.seven.base.BasePresenter
import com.sohohouse.seven.common.extensions.addTo
import com.sohohouse.seven.common.extensions.buildAddress
import com.sohohouse.seven.common.extensions.getFormattedDateTime
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.Venue
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject

class InductionConfirmationPresenter @Inject constructor(
    private val venueRepo: VenueRepo,
    private val zipRequestsUtil: ZipRequestsUtil
) : BasePresenter<InductionConfirmationViewController>() {
    private val disposable = CompositeDisposable()
    lateinit var venueId: String
    var bookingID: String? = null
    lateinit var venue: Venue
    lateinit var locationString: String

    override fun onDetach(view: InductionConfirmationViewController?) {
        disposable.clear()
        super.onDetach(view)
    }

    fun fetch() {
        Single.just(venueRepo.venues().filterWithTopLevel())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { response ->
                val venues = response.filter { it.id == venueId }
                if (venues.isNotEmpty()) {
                    venue = venues[0]
                    executeWhenAvailable { view, _, _ -> view.setUpLayout(venue) }
                }
            }.addTo(disposable)
    }

    fun loadAppointmentConfirmation(event: Event, bookingID: String, isIndependent: Boolean) {
        this.bookingID = bookingID
        this.locationString = event.address
        executeWhenAvailable { view, _, _ ->
            view.displayAppointment(
                venue.name,
                venue.venueColors.house,
                venue.house.get(venue.document)?.houseImageSet?.largePng ?: "",
                event.startsAt?.getFormattedDateTime(venue.timeZone) ?: "",
                locationString,
                event.isOffsite
            )
            view.styleForIndependence(isIndependent)
        }
    }

    fun loadFollowUpConfirmation() {
        this.locationString = venue.buildAddress(singleLine = false)
        executeWhenAvailable { view, _, _ ->
            view.displayFollowUp(
                venue.name,
                venue.venueColors.house,
                venue.house.get(venue.document)?.houseImageSet?.largePng ?: "",
                locationString
            )
        }
    }

    fun doneClicked() {
        executeWhenAvailable { view, _, _ -> view.navigateToNextOnboarding() }
    }

    fun changeClicked() {
        bookingID?.let {
            executeWhenAvailable { view, _, _ -> view.navigateToInductionBooking(it) }
        }
    }

    fun openMapClicked() {
        val uri = String.format(
            Locale.ENGLISH,
            "geo:/${venue.location.longitude},${venue.location.latitude}?q=$locationString"
        )
        executeWhenAvailable { view, _, _ -> view.openLocationInMaps(uri) }
    }
}
