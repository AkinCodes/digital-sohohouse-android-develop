package com.sohohouse.seven.base.filter.types.date

import com.sohohouse.seven.base.BasePresenter
import com.sohohouse.seven.common.extensions.getFormattedDate
import java.util.*
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.MONTH

class FilterDatePresenter : BasePresenter<FilterDateViewController>() {
    companion object {
        private const val MONTH_EXTRA: Int = 1
    }

    fun onDataReady(selectedStartDate: Date?, selectedEndDate: Date?) {
        val minDate = Calendar.getInstance()
        val maxDate = getMaxDate()
        executeWhenAvailable { view, _, _ ->
            view.setUpLayout()
            view.updateDateText(
                selectedStartDate?.getFormattedDate()
                    ?: minDate.time.getFormattedDate(), true
            )
            selectedEndDate?.let { view.updateDateText(it.getFormattedDate(), false) }
            view.updateDatePicker(selectedStartDate ?: minDate.time, true, minDate, maxDate)
            view.updateDatePicker(selectedEndDate ?: maxDate.time, false, minDate, maxDate)
        }
    }

    fun dateSelectionUpdated(date: Date, isStart: Boolean) {
        executeWhenAvailable { view, _, _ ->
            view.updateDateText(date.getFormattedDate(), isStart)
            view.updateCutOffDate(date, !isStart)
            view.sendUpdate(date, isStart)
        }
    }

    fun clearDate(isStart: Boolean) {
        val minDate = Calendar.getInstance()
        val maxDate = getMaxDate()
        executeWhenAvailable { view, _, _ ->
            view.updateDateText(isStart, minDate.time.getFormattedDate())
            view.updateCutOffDate(if (isStart) minDate.time else maxDate.time, !isStart)
            view.resetSelectedDate(isStart, if (isStart) minDate.time else maxDate.time)
            view.sendUpdate(if (isStart) minDate.time else null, isStart)
        }
    }

    private fun getMaxDate(): Calendar {
        val maxDate = Calendar.getInstance()
        maxDate.add(MONTH, MONTH_EXTRA)
        maxDate.set(DAY_OF_MONTH, maxDate.getActualMaximum(DAY_OF_MONTH))
        return maxDate
    }
}