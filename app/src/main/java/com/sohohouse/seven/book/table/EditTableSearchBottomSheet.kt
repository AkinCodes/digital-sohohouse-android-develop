package com.sohohouse.seven.book.table

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseBottomSheet
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.databinding.BottomSheetTableSearchFormBinding

class EditTableSearchBottomSheet : BaseBottomSheet(), TableSearchFormView.Host,
    TableSearchFormView.CityHost {
    companion object {
        const val REQ_KEY = "TABLE_SEARCH"
        const val EXTRA_INPUT = "EXTRA_INPUT"

        const val TAG = "TableSearchBottomSheet"

        fun newInstance(input: TableSearchFormView.Input): EditTableSearchBottomSheet {
            return EditTableSearchBottomSheet().apply {
                arguments = bundleOf(EXTRA_INPUT to input)
            }
        }
    }

    private val binding by viewBinding(BottomSheetTableSearchFormBinding::bind)

    override val contentLayout: Int = R.layout.bottom_sheet_table_search_form

    private var input: TableSearchFormView.Input? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnClose.clicks { dismiss() }
            searchForm.host = this@EditTableSearchBottomSheet
            searchForm.cityHost = this@EditTableSearchBottomSheet
            input = (savedInstanceState ?: arguments)?.getParcelable(EXTRA_INPUT)
            populate(input)
            btnCheckAvailability.clicks { onConfirmed() }
        }
    }

    private fun BottomSheetTableSearchFormBinding.populate(input: TableSearchFormView.Input?) {
        input?.let {
            searchForm.setVenue(it.venueID, it.venueName)
            searchForm.setDate(it.year, it.month, it.day)
            searchForm.setTime(it.hour, it.minute)
            searchForm.setSeats(it.seats)
        }
    }

    fun onConfirmed() {
        setFragmentResult(
            REQ_KEY, bundleOf(
                EXTRA_INPUT to this.input
            )
        )
    }

    override fun onDateSet(year: Int, month: Int, dayOfMonth: Int) {
        input?.day = dayOfMonth
        input?.month = month
        input?.year = year
    }

    override fun onTimeSet(hourOfDay: Int, minute: Int) {
        input?.hour = hourOfDay
        input?.minute = minute
    }

    override fun onVenueSet(venueID: String, venueName: String) {
        input?.venueID = venueID
        input?.venueName = venueName
    }

    override fun onCitySet(cityName: String, venueIds: List<String>) {
        input?.venueName = cityName
        input?.venueIds = venueIds
    }

    override fun onSeatsSet(seats: Int) {
        input?.seats = seats
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(EXTRA_INPUT, input)
        super.onSaveInstanceState(outState)
    }

    override val _fragmentManager: FragmentManager
        get() = parentFragmentManager

}