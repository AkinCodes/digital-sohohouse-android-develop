package com.sohohouse.seven.book.table

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.FragmentManager
import com.sohohouse.seven.R
import com.sohohouse.seven.book.table.guestspeeker.GuestsPickerDialog
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.getDayAndMonthFormattedDate
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.extensions.withResultListener
import com.sohohouse.seven.common.views.FormView
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.databinding.TableSearchFormBinding
import com.sohohouse.seven.guests.LocationPickerFragment
import com.sohohouse.seven.guests.LocationType
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat
import java.util.*

class TableSearchFormView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : FormView(context, attrs, defStyleAttrs), TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener, LocationPickerFragment.Listener {

    companion object {
        private const val MAX_MONTHS_IN_FUTURE = 3
        private const val DEFAULT_PICKER_HOUR = 12
        private const val DEFAULT_PICKER_MIN = 0
    }

    lateinit var host: Host
    lateinit var cityHost: CityHost

    private var locationFilter: String? = null
    private var dayOfMonth: Int? = null
    private var month: Int? = null  //zero-indexed
    private var year: Int? = null
    private var hour: Int? = null
    private var minute: Int? = null
    private var numSeats: Int? = null
    private val binding: TableSearchFormBinding =
        TableSearchFormBinding.inflate(LayoutInflater.from(context), this)

    init {
        with(binding) {
            venue.clicks { onSelectLocationClick() }
            date.clicks { onSelectDateClick() }
            time.clicks { onSelectTimeClick() }
            seats.clicks { onSelectSeatsClick() }

            val ta =
                context.obtainStyledAttributes(
                    attrs,
                    R.styleable.TableSearchFormView,
                    defStyleAttrs,
                    0
                )
            val includeVenue = ta.getBoolean(R.styleable.TableSearchFormView_includeVenue, true)
            ta.recycle()

            venue.setVisible(includeVenue)
        }
    }

    private fun maxDate(): Long {
        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.MONTH, /*extraMonths*/MAX_MONTHS_IN_FUTURE)
        maxDate.set(Calendar.DAY_OF_MONTH, maxDate.getActualMaximum(Calendar.DAY_OF_MONTH))
        return maxDate.time.time
    }

    private fun onSelectSeatsClick() {
        GuestsPickerDialog.newInstance(numSeats)
            .withResultListener(GuestsPickerDialog.REQUEST_GUESTS_KEY) { _, bundle ->
                val seats = bundle.getInt(GuestsPickerDialog.EXTRA_GUESTS)
                setSeats(seats)
                host.onSeatsSet(seats)
            }.showSafe(host._fragmentManager, GuestsPickerDialog.TAG)
    }

    private fun onSelectTimeClick() {
        TimePickerDialog(
            context,
            this,
            hour ?: DEFAULT_PICKER_HOUR,
            minute ?: DEFAULT_PICKER_MIN,
            android.text.format.DateFormat.is24HourFormat(context)
        ).show()
    }

    private fun onSelectDateClick() {
        val selectedDate = Calendar.getInstance()
            .apply {
                if (dayOfMonth != null) set(year!!, month!!, dayOfMonth!!)
            }
            .time
        val selectedDateCalendar = Calendar.getInstance().apply { time = selectedDate }

        DatePickerDialog(
            context,
            R.style.Dialog_DatePicker,
            this,
            selectedDateCalendar.get(Calendar.YEAR),
            selectedDateCalendar.get(Calendar.MONTH),
            selectedDateCalendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = today()
            datePicker.maxDate = maxDate()
        }.show()
    }

    private fun today() = Calendar.getInstance().time.time

    private fun onSelectLocationClick() {
        LocationPickerFragment.newInstance(locationFilter, true)
            .apply { listener = this@TableSearchFormView }
            .showSafe(host._fragmentManager, LocationPickerFragment::class.java.simpleName)
    }

    fun setVenue(id: String?, name: String?) {
        this.locationFilter = id
        binding.venue.value = name
    }

    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        this.year = year
        this.month = month
        this.dayOfMonth = dayOfMonth
        binding.date.value = formatDate(year, month, dayOfMonth)
    }

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val date = Calendar.getInstance().apply { set(year, month, dayOfMonth) }.time
        return date.getDayAndMonthFormattedDate()
    }

    fun setTime(hourOfDay: Int, minute: Int) {
        this.hour = hourOfDay
        this.minute = minute
        binding.time.value = formatTime(hourOfDay, minute)
    }

    private fun formatTime(hourOfDay: Int, minute: Int): String {
        val df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
        val time = Calendar.getInstance().apply { set(1970, 1, 1, hourOfDay, minute) }.time
        return df.format(time)
    }

    fun setSeats(numSeats: Int) {
        binding.seats.value = formatSeats(numSeats)
        this.numSeats = numSeats
    }

    private fun formatSeats(seats: Int): String {
        return context.resources.getQuantityString(
            R.plurals.book_a_table_number_of_seats_value, seats, seats
        )
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        setTime(hourOfDay, minute)
        host.onTimeSet(hourOfDay, minute)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        setDate(year, month, dayOfMonth)
        host.onDateSet(year, month, dayOfMonth)
    }

    override fun onLocationSelected(type: LocationType) {
        when (type) {
            is LocationType.City -> {
                cityHost.onCitySet(type.name, type.venueIds)
                setVenue(type.name, type.name)
            }
            is LocationType.SingleVenue -> {
                setVenue(type.venue.id, type.name)
                host.onVenueSet(type.venue.id, type.name)
            }
        }
    }

    interface Host {
        fun onDateSet(year: Int, month: Int, dayOfMonth: Int)
        fun onTimeSet(hourOfDay: Int, minute: Int)
        fun onVenueSet(venueID: String, venueName: String)
        fun onSeatsSet(seats: Int)

        @Suppress("PropertyName")   // to avoid clashes
        val _fragmentManager: FragmentManager
    }

    interface CityHost {
        fun onCitySet(cityName: String, venueIds: List<String>)
    }

    @Parcelize
    data class Input(
        var venueID: String,
        var venueName: String,
        var venueIds: List<String>? = null,
        var year: Int,
        var month: Int,
        var day: Int,
        var hour: Int,
        var minute: Int,
        var seats: Int
    ) : Parcelable
}