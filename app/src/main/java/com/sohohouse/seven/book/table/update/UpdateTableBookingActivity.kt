package com.sohohouse.seven.book.table.update

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.book.table.TableBookingUtil
import com.sohohouse.seven.book.table.TableBookingUtil.TIME_SLOTS_SPAN_COUNT
import com.sohohouse.seven.book.table.TableSearchFormView
import com.sohohouse.seven.book.table.completebooking.TableCompleteBookingActivity
import com.sohohouse.seven.book.table.model.BookedTable
import com.sohohouse.seven.book.table.timeslots.BookSlot
import com.sohohouse.seven.book.table.timeslots.TimeSlotRenderer
import com.sohohouse.seven.book.table.update.UpdateTableBookingViewModel.State.*
import com.sohohouse.seven.common.apihelpers.SohoWebHelper
import com.sohohouse.seven.common.design.adapter.RendererAdapter
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.webview.openWebView
import com.sohohouse.seven.databinding.ActivityTableBookingUpdateBinding
import java.util.*

class UpdateTableBookingActivity : BaseMVVMActivity<UpdateTableBookingViewModel>(),
    TableSearchFormView.Host,
    Loadable.View {

    companion object {
        private const val EXTRA_BOOKING_DETAILS = "EXTRA_BOOKING_DETAILS"

        fun newIntent(context: Context, details: BookedTable): Intent {
            return Intent(context, UpdateTableBookingActivity::class.java).apply {
                putExtra(EXTRA_BOOKING_DETAILS, details)
            }
        }
    }

    override val viewModelClass: Class<UpdateTableBookingViewModel>
        get() = UpdateTableBookingViewModel::class.java

    override val loadingView: LoadingView
        get() = binding.rootLoading

    private val binding by viewBinding(ActivityTableBookingUpdateBinding::bind)

    private val adapter = RendererAdapter<BookSlot>()

    override fun getContentLayout(): Int = R.layout.activity_table_booking_update

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(binding) {
            searchForm.host = this@UpdateTableBookingActivity

            btnClose.setOnClickListener { finish() }
            btnBook.setOnClickListener { onBookClick() }
            btnContactUs.setOnClickListener { onContactUsClick() }

            listTimeSlots.layoutManager =
                GridLayoutManager(this@UpdateTableBookingActivity, TIME_SLOTS_SPAN_COUNT)
            adapter.registerRenderer(TimeSlotRenderer(this@UpdateTableBookingActivity::slotClick))
            listTimeSlots.adapter = adapter
        }

        observeLoadingState(this) { onLoadingStateChanged(it) }

        viewModel.state.observe(lifecycleOwner) { showState(it) }
        viewModel.bookingData.observe(lifecycleOwner) { showDetails(it) }
        viewModel.timeSlots.observe(lifecycleOwner) { populateTimeSlots(it) }
        viewModel.bookEnabled.observe(lifecycleOwner) { bookEnable(it) }
        viewModel.selectedDate.observe(lifecycleOwner) { showDate(it) }
        viewModel.selectedTime.observe(lifecycleOwner) { showTime(it) }
        viewModel.selectedSeats.observe(lifecycleOwner) { showSeats(it) }
        viewModel.openConfirmation.observe(lifecycleOwner) { navigateToConfirmation(it) }
        viewModel.showError.observe(lifecycleOwner) { showErrorDialog(it) }

        viewModel.init(intent.getSerializableExtra(EXTRA_BOOKING_DETAILS) as BookedTable)
    }

    private fun showState(state: UpdateTableBookingViewModel.State) =
        with(binding) {
            when (state) {
                INITIAL_STATE -> {
                    stateAlternativeAvailability.setGone()
                    stateHasAvailibility.setGone()
                    stateNoAvailibility.setGone()
                    stateInitial.setVisible()
                }
                NO_AVAILIBILITY -> {
                    stateAlternativeAvailability.setGone()
                    stateHasAvailibility.setGone()
                    stateInitial.setGone()
                    stateNoAvailibility.setVisible()
                }
                HAS_AVAILABILITY -> {
                    stateAlternativeAvailability.setGone()
                    stateInitial.setGone()
                    stateNoAvailibility.setGone()
                    stateHasAvailibility.setVisible()
                }
                ALTERNATIVE_AVAILIBILITY -> {
                    stateInitial.setGone()
                    stateHasAvailibility.setGone()
                    stateNoAvailibility.setGone()
                    stateAlternativeAvailability.setVisible()
                }
            }
        }

    private fun showErrorDialog(errorResId: Int) {
        TableBookingUtil.createErrorDialog(
            this,
            errorResId
        )
        { showContactUs() }
            .show()
    }

    private fun showContactUs() {
        openWebView(supportFragmentManager, SohoWebHelper.KickoutType.CONTACT_SUPPORT)
    }

    private fun onLoadingStateChanged(loadingState: LoadingState) =
        with(binding) {
            searchForm.setDirectChildrenEnabled(
                loadingState != LoadingState.Loading,
                changeAlpha = true
            )
            listTimeSlots.setDirectChildrenEnabled(
                loadingState != LoadingState.Loading,
                changeAlpha = true
            )
        }

    private fun showDetails(data: UpdateBookingData) =
        with(binding) {
            restaurantImage.setImageFromUrl(data.imageUrl)
            restaurantTitle.text = data.name
            dateTime.text = data.dateTime
            persons.text = data.persons
        }

    private fun populateTimeSlots(slots: List<BookSlot>) {
        adapter.submitItems(slots)
    }

    private fun onBookClick() {
        viewModel.lockSlot()
    }

    private fun bookEnable(enable: Boolean) {
        binding.btnBook.isEnabled = enable
    }

    private fun slotClick(slot: BookSlot) {
        viewModel.selectSlot(slot)
    }

    private fun showDate(date: Date?) {
        date?.let {
            val (year, month, day) = it.yearMonthDay
            binding.searchForm.setDate(year, month, day)
        }
    }

    private fun showTime(time: Date?) {
        time?.let {
            val (hour, minute) = it.hourMinute
            binding.searchForm.setTime(hour, minute)
        }
    }

    private fun showSeats(guests: Int) {
        binding.searchForm.setSeats(guests)
    }

    private fun navigateToConfirmation(details: BookedTable) {
        startActivity(TableCompleteBookingActivity.newIntentUpdate(this, details))
        finish()
    }

    private fun onContactUsClick() {
        openWebView(supportFragmentManager, SohoWebHelper.KickoutType.CONTACT_SUPPORT)
    }

    override fun onDateSet(year: Int, month: Int, dayOfMonth: Int) {
        viewModel.fillDate(year, month, dayOfMonth)
    }

    override fun onTimeSet(hourOfDay: Int, minute: Int) {
        viewModel.fillTime(hourOfDay, minute)
    }

    override fun onVenueSet(venueID: String, venueName: String) {
        //do nothing, no venue selection
    }

    override fun onSeatsSet(seats: Int) {
        viewModel.fillSeats(seats)
    }

    override val _fragmentManager: FragmentManager
        get() = supportFragmentManager

}