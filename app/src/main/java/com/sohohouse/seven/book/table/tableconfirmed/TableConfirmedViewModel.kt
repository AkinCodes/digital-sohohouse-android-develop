package com.sohohouse.seven.book.table.tableconfirmed

import android.os.Bundle
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.book.table.TableBookingDetails
import com.sohohouse.seven.book.table.model.BookedTable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.dateTime
import com.sohohouse.seven.common.extensions.getFormattedDateTime
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class TableConfirmedViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager, dispatcher) {

    private var details: TableBookingDetails? = null

    fun init(details: TableBookingDetails?, detailsUpdate: BookedTable?): SummaryData {
        this.details = details

        return if (details != null) {
            SummaryData(details)
        } else {
            SummaryData(detailsUpdate!!)
        }
    }

    fun done() {
        details?.let {
            analyticsManager.logEventAction(AnalyticsManager.Action.TableBookingDone,
                Bundle().apply {
                    putString("house_id", it.venueId)
                    putString("booking_id", it.booking?.id ?: "")
                    putLong("total_time_spent", System.currentTimeMillis() - it.startTimeMills)
                })
        }
    }
}

data class SummaryData(
    val name: String,
    val address: String,
    val country: String,
    val imageUrl: String,
    val date: String,
    val persons: String,
    val comments: String,
    val confirmationNumber: String
) {
    constructor(details: TableBookingDetails) : this(
        name = details.name,
        address = details.address,
        country = details.country,
        imageUrl = details.imageUrl,
        date = details.slotLock?.dateTime?.getFormattedDateTime("") ?: "",
        persons = "${details.persons}",
        comments = details.booking?.special_request ?: "",
        confirmationNumber = details.booking?.confirmation_number.toString()
    )

    constructor(details: BookedTable) : this(
        name = details.name,
        address = details.address,
        country = details.country,
        imageUrl = details.imageUrl,
        date = details.slotLock?.dateTime?.getFormattedDateTime("") ?: "",
        persons = "${details.slotLock?.party_size}",
        comments = details.specialComment,
        confirmationNumber = details.confirmationNumber.toString()
    )
}