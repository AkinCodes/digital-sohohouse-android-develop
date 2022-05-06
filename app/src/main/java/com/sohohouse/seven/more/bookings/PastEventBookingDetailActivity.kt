package com.sohohouse.seven.more.bookings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.databinding.ActivityPastBookingThankyouBinding
import com.sohohouse.seven.network.core.models.EventBooking

class PastEventBookingDetailActivity : BaseMVVMActivity<PastBookingDetailViewModel>() {

    companion object {
        private const val KEY_EVENT_BOOKING = "KEY_EVENT_BOOKING"

        fun getIntent(context: Context, booking: EventBooking): Intent {
            return Intent(context, PastEventBookingDetailActivity::class.java).apply {
                putExtra(KEY_EVENT_BOOKING, booking)
            }
        }
    }

    override val viewModelClass: Class<PastBookingDetailViewModel>
        get() = PastBookingDetailViewModel::class.java

    override fun getContentLayout(): Int {
        return R.layout.activity_past_booking_thankyou
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        val binding = ActivityPastBookingThankyouBinding.inflate(layoutInflater)

        viewModel.init(intent.getSerializableExtra(KEY_EVENT_BOOKING) as EventBooking)
        viewModel.data.observe(this) {
            bindData(it, binding)
        }

        binding.browseEventsBtn.clicks {
            val intent = Intent()
            intent.putExtra(
                MyBookingsActivity.MORE_PAST_BOOKINGS_GO_TO_EXPLORE,
                MyBookingsActivity.MORE_PAST_BOOKINGS_GO_TO_EXPLORE
            )
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        binding.dismissBtn.clicks {
            finish()
        }
    }

    private fun bindData(
        data: PastBookingDetailViewModel.BookingData,
        binding: ActivityPastBookingThankyouBinding
    ) = with(binding) {
        eventImage.setImageFromUrl(data.imageUrl, placeholder = 0)
        header.text = data.thankYouMsg
        supporting.text = data.supportingMsg
        eventDateAndTime.text = data.dateAndTime
        eventLocation.text = data.location
    }
}