package com.sohohouse.seven.base.filter.types.date

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.filter.FilterListener
import com.sohohouse.seven.base.filter.types.FilterUnitFragment
import com.sohohouse.seven.base.filter.types.date.FilterDateViewModel.UiEvent
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setTimeToMidNight
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.utils.collect
import com.sohohouse.seven.databinding.FilterDateFragmentBinding
import java.util.*

class FilterDateFragment : BaseMVVMFragment<FilterDateViewModel>(),
    FilterUnitFragment,
    DatePickerDialog.OnDateSetListener {

    private val viewBinding: FilterDateFragmentBinding by viewBinding(FilterDateFragmentBinding::bind)

    override val contentLayoutId get() = R.layout.filter_date_fragment

    override val viewModelClass: Class<FilterDateViewModel>
        get() = FilterDateViewModel::class.java

    private lateinit var startDatePicker: DatePickerDialog
    private lateinit var endDatePicker: DatePickerDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.eventsFlow.collect(viewLifecycleOwner) { event ->
            when (event) {
                is UiEvent.SetUpLayout -> setUpLayout()
                is UiEvent.UpdateCutOffDate -> updateCutOffDate(event.cutOffDate, event.isStart)
                is UiEvent.UpdateDatePicker -> with(event) {
                    updateDatePicker(selectedDate, isStart, minDate, maxDate)
                }
                is UiEvent.SendUpdate -> sendUpdate(event.date, event.isStart)
                is UiEvent.UpdateMinDateText -> with(event) {
                    updateDateText(isStart, minDateString)
                }
                is UiEvent.UpdateDateText -> with(event) {
                    updateDateText(updatedDateString, isStart)
                }
                is UiEvent.ResetSelectedDate -> with(event) {
                    resetSelectedDate(isStart, defaultDate)
                }
            }
        }
    }

    override fun resetSelection() {
        viewModel.clearDate(true)
        viewModel.clearDate(false)
    }

    override fun getTitleRes() = R.string.explore_events_filter_header

    override fun onDataReady() {
        viewModel.onDataReady(
            (activity as FilterListener).getSelectedStartDate(),
            (activity as FilterListener).getSelectedEndDate()
        )
    }

    private fun setUpLayout() = with(viewBinding) {
        filterDateFromContainer.clicks { startDatePicker.show() }
        filterDateToContainer.clicks { endDatePicker.show() }
        filterDateEndClear.clicks { viewModel.clearDate(false) }
    }

    private fun updateDateText(isStart: Boolean, minDateString: String) {
        updateDateText(
            if (isStart) minDateString else getString(R.string.explore_events_filter_end_date_label),
            isStart
        )
    }

    private fun updateDateText(updatedDateString: String, isStart: Boolean) = with(viewBinding) {
        if (isStart) {
            filterDateStart.text = updatedDateString
        } else {
            filterDateEnd.text = updatedDateString
            filterDateEndClear.setVisible(updatedDateString == getString(R.string.explore_events_filter_end_date_label))
        }
    }

    private fun updateCutOffDate(cutOffDate: Date, isStart: Boolean) {
        val datePicker = (if (isStart) startDatePicker else endDatePicker).datePicker
        val currentSelectedYear = datePicker.year
        val currentSelectedMonth = datePicker.month
        val currentSelectedDay = datePicker.dayOfMonth
        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.set(currentSelectedYear, currentSelectedMonth, currentSelectedDay)

        val cutOffCalendar = Calendar.getInstance()
        cutOffCalendar.time = cutOffDate

        val unChangedCalendar = Calendar.getInstance()

        if (isStart) {
            unChangedCalendar.timeInMillis = startDatePicker.datePicker.minDate
            updateDatePicker(selectedCalendar.time, isStart, unChangedCalendar, cutOffCalendar)
        } else {
            unChangedCalendar.timeInMillis = endDatePicker.datePicker.maxDate
            updateDatePicker(selectedCalendar.time, isStart, cutOffCalendar, unChangedCalendar)
        }
    }

    private fun updateDatePicker(
        selectedDate: Date,
        isStart: Boolean,
        minDate: Calendar,
        maxDate: Calendar
    ) {
        val calendar = Calendar.getInstance()
        calendar.time = selectedDate

        val newDatePicker = DatePickerDialog(
            requireContext(),
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        newDatePicker.datePicker.minDate = minDate.timeInMillis
        newDatePicker.datePicker.maxDate = maxDate.timeInMillis

        if (isStart) {
            startDatePicker = newDatePicker
        } else {
            endDatePicker = newDatePicker
        }
    }

    private fun sendUpdate(date: Date?, isStart: Boolean) {
        (activity as FilterListener).onSelectedDateChanged(date, isStart)
    }

    private fun resetSelectedDate(isStart: Boolean, defaultDate: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = defaultDate
        if (isStart) {
            startDatePicker.updateDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        } else {
            endDatePicker.updateDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        if (view == startDatePicker.datePicker) {
            viewModel.dateSelectionUpdated(calendar.setTimeToMidNight(true), true)
        } else if (view == endDatePicker.datePicker) {
            viewModel.dateSelectionUpdated(calendar.setTimeToMidNight(false), false)
        }
    }

    companion object {
        const val TAG = "FilterDateFragment"
    }
}