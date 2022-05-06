package com.sohohouse.seven.book.eventdetails.bookingsuccess

import android.app.Activity
import android.content.Context
import android.content.Intent
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseViewControllerActivity
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.views.eventdetaillist.BaseEventDetailsAdapterItem
import com.sohohouse.seven.databinding.ActivityBookingSuccessBinding

class BookingSuccessActivity : BaseViewControllerActivity<BookingSuccessPresenter>(),
    BookingSuccessViewController, BookingSuccessAdapterListener {
    companion object {
        const val BOOKING_SUCCESS_ITEM = "bookingSuccessItem"
        const val BOOKING_SUCCESS_REQUEST_CODE = 6009

        fun getIntent(context: Context, bookingSuccessItem: BookingSuccessItem): Intent {
            return Intent(context, BookingSuccessActivity::class.java).apply {
                putExtra(BOOKING_SUCCESS_ITEM, bookingSuccessItem)
            }
        }
    }

    private val binding by viewBinding(ActivityBookingSuccessBinding::bind)

    override fun getContentLayout(): Int = R.layout.activity_booking_success

    override fun createPresenter(): BookingSuccessPresenter {
        return BookingSuccessPresenter()
    }

    override fun initLayout() {
        val item = intent.getSerializableExtra(BOOKING_SUCCESS_ITEM) as BookingSuccessItem

        binding.doneButton.text = when {
            item.isTicketless -> getString(R.string.explore_events_confirm_modal_ticketless_cta)
            item.isInduction -> getString(R.string.onboarding_intro_booked_next_cta)
            else -> getString(R.string.explore_events_event_done_cta)
        }

        binding.doneButton.clicks {
            //result set for onboarding flow
            setResult(Activity.RESULT_OK, Intent())
            finish()
        }

        presenter.setUpData(item)
    }

    override fun setUpRecyclerView(data: List<BaseEventDetailsAdapterItem>) {
        binding.bookingSuccessRecyclerview.adapter = BookingSuccessAdapter(this, data)
    }

    override fun onBackClicked() {
        setResult(Activity.RESULT_OK, Intent())
        finish()
    }
}
