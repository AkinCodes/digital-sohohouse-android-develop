package com.sohohouse.seven.base.filter.types.date

import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.getFormattedDate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class FilterDateViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val ioDispatcher: CoroutineDispatcher,
) : BaseViewModel(analyticsManager) {

    private val _eventsFlow = MutableSharedFlow<UiEvent>(3, 3)
    val eventsFlow = _eventsFlow.asSharedFlow()

    fun onDataReady(selectedStartDate: Date?, selectedEndDate: Date?) {
        val minDate = Calendar.getInstance()
        val maxDate = getMaxDate()
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            _eventsFlow.emit(UiEvent.SetUpLayout)
            _eventsFlow.emit(
                UiEvent.UpdateDateText(
                    selectedStartDate?.getFormattedDate()
                        ?: minDate.time.getFormattedDate(),
                    true
                )
            )
            selectedEndDate?.let {
                _eventsFlow.emit(UiEvent.UpdateDateText(it.getFormattedDate(), false))
            }
            _eventsFlow.emit(
                UiEvent.UpdateDatePicker(
                    selectedStartDate ?: minDate.time, true, minDate, maxDate
                )
            )
            _eventsFlow.emit(
                UiEvent.UpdateDatePicker(
                    selectedEndDate ?: maxDate.time, false, minDate, maxDate
                )
            )
        }
    }

    fun dateSelectionUpdated(date: Date, isStart: Boolean) {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            _eventsFlow.emit(UiEvent.UpdateDateText(date.getFormattedDate(), isStart))
            _eventsFlow.emit(UiEvent.UpdateCutOffDate(date, !isStart))
            _eventsFlow.emit(UiEvent.SendUpdate(date, isStart))
        }
    }

    fun clearDate(isStart: Boolean) {
        val minDate = Calendar.getInstance()
        val maxDate = getMaxDate()
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            _eventsFlow.emit(UiEvent.UpdateMinDateText(isStart, minDate.time.getFormattedDate()))
            _eventsFlow.emit(
                UiEvent.UpdateCutOffDate(
                    if (isStart) minDate.time else maxDate.time,
                    !isStart
                )
            )
            _eventsFlow.emit(
                UiEvent.ResetSelectedDate(
                    isStart,
                    if (isStart) minDate.time else maxDate.time
                )
            )
            _eventsFlow.emit(UiEvent.SendUpdate(if (isStart) minDate.time else null, isStart))
        }
    }

    private fun getMaxDate(): Calendar {
        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.MONTH, MONTH_EXTRA)
        maxDate.set(Calendar.DAY_OF_MONTH, maxDate.getActualMaximum(Calendar.DAY_OF_MONTH))
        return maxDate
    }

    companion object {
        private const val MONTH_EXTRA: Int = 1
    }

    sealed class UiEvent {
        object SetUpLayout : UiEvent()
        data class UpdateCutOffDate(val cutOffDate: Date, val isStart: Boolean) : UiEvent()
        data class UpdateDatePicker(
            val selectedDate: Date,
            val isStart: Boolean,
            val minDate: Calendar,
            val maxDate: Calendar
        ) : UiEvent()
        data class SendUpdate(val date: Date?, val isStart: Boolean) : UiEvent()
        data class UpdateMinDateText(val isStart: Boolean, val minDateString: String) : UiEvent()
        data class UpdateDateText(val updatedDateString: String, val isStart: Boolean) : UiEvent()
        data class ResetSelectedDate(val isStart: Boolean, val defaultDate: Date) : UiEvent()
    }
}