package com.sohohouse.seven.book.table.update

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LiveEvent
import com.sohohouse.seven.book.table.TableBookingErrorMapper
import com.sohohouse.seven.book.table.TableBookingUtil
import com.sohohouse.seven.book.table.model.BookedTable
import com.sohohouse.seven.book.table.timeslots.BookSlot
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.getApiFormattedDateIgnoreTimezone
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.ErrorResponse
import com.sohohouse.seven.network.core.SohoApiService
import com.sohohouse.seven.network.core.models.SlotLock
import com.sohohouse.seven.network.core.models.TableAvailabilities
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import moe.banana.jsonapi2.HasOne
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

class UpdateTableBookingViewModel @Inject constructor(
    val apiService: SohoApiService, analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager, dispatcher), Loadable.ViewModel by Loadable.ViewModelImpl() {

    private lateinit var details: BookedTable
    private var _selectedSlot: BookSlot? = null
    private val _selectedDate: Date? get() = selectedDate.value
    private val _selectedTime: Date? get() = selectedTime.value

    val bookingData = MutableLiveData<UpdateBookingData>()
    val timeSlots = MutableLiveData<List<BookSlot>>(emptyList())

    val bookEnabled = MutableLiveData(false)
    val openConfirmation = LiveEvent<BookedTable>()

    val selectedDate = MutableLiveData<Date>()
    val selectedTime = MutableLiveData<Date>()
    val selectedSeats = MutableLiveData<Int>()

    val showError = LiveEvent<Int>()

    val state = MutableLiveData(State.INITIAL_STATE)

    fun init(details: BookedTable) {
        if (this::details.isInitialized) return //TODO use assisted inject

        this.details = details

        setupDetails()

        selectedDate.postValue(details.bookedTableDate.date)
        selectedTime.postValue(details.bookedTableDate.date)
        selectedSeats.postValue(details.seats)

        loadAvailabilities(details.restaurantId, details.bookedTableDate.date, details.seats)
    }

    fun lockSlot() {
        viewModelScope.launch(viewModelContext) {
            setLoading()

            val result = apiService.lockTable(
                SlotLock(
                    date_time = _selectedSlot?.dateTime?.getApiFormattedDateIgnoreTimezone(),
                    party_size = selectedSeats.value,
                    restaurant = HasOne("restaurants", details.restaurantId)
                )
            )

            when (result) {
                is ApiResponse.Success -> onLockSuccess(result.response)
                is ApiResponse.Error -> onLockError(result.response)
            }

            setIdle()
        }
    }

    fun selectSlot(slot: BookSlot?) {
        slot?.let { _slot ->
            _selectedSlot = _slot
            bookEnabled.postValue(true)

            timeSlots.value?.forEach { it.isSelected = false }
            timeSlots.value?.find { it.text == _slot.text }?.let { it.isSelected = true }

            timeSlots.postValue(timeSlots.value)
        }
    }

    fun fillDate(year: Int, month: Int, dayOfMonth: Int) {
        selectedDate.value = Calendar.getInstance().apply { set(year, month, dayOfMonth) }.time
        loadAvailabilities(details.restaurantId, combineDates(), selectedSeats.value ?: 0)
    }

    fun fillTime(hourOfDay: Int, minute: Int) {
        selectedTime.value =
            Calendar.getInstance().apply { set(1970, 1, 1, hourOfDay, minute) }.time
        loadAvailabilities(details.restaurantId, combineDates(), selectedSeats.value ?: 0)
    }

    fun fillSeats(seats: Int) {
        selectedSeats.value = seats
        loadAvailabilities(details.restaurantId, combineDates(), seats)
    }

    private fun loadAvailabilities(id: String, date: Date, seats: Int) {
        viewModelScope.launch(viewModelContext) {
            setLoading()
            bookEnabled.postValue(false)
            val result = apiService.checkTableAvailability(
                id,
                date.getApiFormattedDateIgnoreTimezone(),
                seats
            )

            when (result) {
                is ApiResponse.Success -> setupTimeSlots(result.response.first(), date)
                is ApiResponse.Error -> noTimeSlots()
            }
            setIdle()
        }
    }

    private fun setupDetails() {
        bookingData.postValue(
            UpdateBookingData(
                details.name,
                details.bookedTableDate.getFormattedDateTime(""),
                details.seats.toString() + " persons",
                details.imageUrl
            )
        )
    }

    private fun noTimeSlots() {
        timeSlots.postValue(emptyList())
        bookEnabled.postValue(false)
        state.postValue(State.NO_AVAILIBILITY)
    }

    private fun onLockSuccess(slot: SlotLock) {
        details.slotLock = slot
        openConfirmation.postValue(details)
    }

    private fun onLockError(response: ErrorResponse?) {
        bookEnabled.postValue(true)
        response?.let { showError.postValue(TableBookingErrorMapper.handleError(it.errors?.firstOrNull()?.code)) }
    }

    private fun setupTimeSlots(availabilities: TableAvailabilities, date: Date) {
        val result = availabilities.time_slots?.map {
            BookSlot(
                formatDate(it.date_time ?: ""),
                it.date_time ?: ""
            )
        }
        if (result.isNullOrEmpty()) {
            noTimeSlots()
        } else {
            var state = State.ALTERNATIVE_AVAILIBILITY
            val time =
                DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault()).format(date)
            result.find { it.text == time }?.let {
                state = State.HAS_AVAILABILITY
                it.isSelected = true
                _selectedSlot = it
                bookEnabled.postValue(true)
            }
            timeSlots.postValue(result)
            this.state.postValue(state)
        }
    }

    private fun formatDate(time: String): String {
        val date = TableBookingUtil.DATE_FORMATTER.parse(time)
        val df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
        return df.format(date)
    }

    private fun combineDates(): Date = Calendar.getInstance().apply {
        time = _selectedTime
        val hour = get(Calendar.HOUR_OF_DAY)
        val mins = get(Calendar.MINUTE)
        time = _selectedDate
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, mins)
    }.time

    enum class State {
        INITIAL_STATE,
        NO_AVAILIBILITY,
        HAS_AVAILABILITY,
        ALTERNATIVE_AVAILIBILITY
    }
}

data class UpdateBookingData(
    val name: String,
    val dateTime: String,
    val persons: String,
    val imageUrl: String
)