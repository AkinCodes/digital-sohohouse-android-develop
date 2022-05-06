package com.sohohouse.seven.book.table.timeslots

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.LiveEvent
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.book.table.TableBookingDetails
import com.sohohouse.seven.book.table.TableBookingErrorMapper
import com.sohohouse.seven.book.table.TableBookingUtil
import com.sohohouse.seven.book.table.TableSearchFormView
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.hourMinute
import com.sohohouse.seven.common.extensions.yearMonthDay
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.ErrorResponse
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.models.SlotLock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import moe.banana.jsonapi2.HasOne
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

class TableTimeSlotsViewModel @Inject constructor(
    val apiService: SohoApiService,
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager, dispatcher), Loadable.ViewModel by Loadable.ViewModelImpl() {

    private lateinit var details: TableBookingDetails
    private var selectedSlot: BookSlot? = null

    val isConfirmEnabled = MutableLiveData(false)
    val timeSlots = MutableLiveData<List<BookSlot>>(emptyList())
    val confirmation = LiveEvent<TableBookingDetails>()
    val bookingErrorMessage = MutableLiveData<Int>()

    fun init(details: TableBookingDetails): TableBookingDetails {
        this.details = details
        setupTimeSlots()
        return details
    }

    val formInput: TableSearchFormView.Input
        get() = with(details) {
            val (year, month, day) = date.yearMonthDay
            val (hour, minute) = formTimeInput?.hourMinute ?: Pair(12, 0)
            TableSearchFormView.Input(
                venueID = venueId,
                venueName = formVenueInput,
                year = year,
                month = month,
                day = day,
                hour = hour,
                minute = minute,
                seats = persons
            )
        }

    fun selectSlot(slot: BookSlot?) {
        slot?.let { slot ->
            selectedSlot = slot
            isConfirmEnabled.postValue(true)

            timeSlots.value?.forEach { it.isSelected = false }
            timeSlots.value?.find { it.text == slot.text }?.let { it.isSelected = true }

            timeSlots.postValue(timeSlots.value)
        }
    }

    fun confirmSlot() {
        viewModelScope.launch(viewModelContext) {
            setLoading()

            analyticsManager.logEventAction(
                AnalyticsManager.Action.TableAvailabilityConfirmSlot,
                Bundle().apply { putString("house_id", details.venueId) })

            val result = apiService.lockTable(
                SlotLock(
                    date_time = selectedSlot?.time ?: "",
                    party_size = details.persons,
                    restaurant = HasOne("restaurants", details.id)
                )
            )

            when (result) {
                is ApiResponse.Success<SlotLock> -> onTimeLockSuccess(result.response)
                is ApiResponse.Error -> onTimeLockError(result.response)
            }

            setIdle()
        }
    }

    private fun onTimeLockSuccess(slot: SlotLock) {
        details.slotLock = slot
        confirmation.postValue(details)
    }

    private fun onTimeLockError(error: ErrorResponse?) {
        error?.let { bookingErrorMessage.postValue(TableBookingErrorMapper.handleError(it.errors?.firstOrNull()?.code)) }
    }

    private fun formatDate(time: String): String {
        val date = TableBookingUtil.DATE_FORMATTER.parse(time)
        val df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())

        return df.format(date)
    }

    private fun setupTimeSlots() {
        val result = details.availabilities.time_slots?.map {
            BookSlot(formatDate(it.date_time ?: ""), it.date_time ?: "")
        }
        if (!result.isNullOrEmpty()) {
            val selectedTime = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
                .format(details.date)
            result.find { it.text == selectedTime }?.let {
                it.isSelected = true
                selectedSlot = it
                isConfirmEnabled.postValue(true)
            }
            timeSlots.postValue(result)
        }
    }

}

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
data class BookSlot(val text: String, val time: String, var isSelected: Boolean = false) {
    val dateTime: Date
        get() = TableBookingUtil.DATE_FORMATTER.parse(time)
}