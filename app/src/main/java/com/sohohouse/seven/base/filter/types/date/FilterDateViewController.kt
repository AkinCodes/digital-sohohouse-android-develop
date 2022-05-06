package com.sohohouse.seven.base.filter.types.date

import com.sohohouse.seven.base.filter.types.FilterBaseViewController
import com.sohohouse.seven.base.mvpimplementation.ViewController
import java.util.*

interface FilterDateViewController : ViewController, FilterBaseViewController {
    fun setUpLayout()
    fun updateCutOffDate(cutOffDate: Date, isStart: Boolean)
    fun updateDatePicker(selectedDate: Date, isStart: Boolean, minDate: Calendar, maxDate: Calendar)
    fun sendUpdate(date: Date?, isStart: Boolean)
    fun updateDateText(isStart: Boolean, minDateString: String)
    fun updateDateText(updatedDateString: String, isStart: Boolean)
    fun resetSelectedDate(isStart: Boolean, defaultDate: Date)
}