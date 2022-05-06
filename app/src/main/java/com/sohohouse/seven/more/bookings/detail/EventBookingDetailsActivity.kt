package com.sohohouse.seven.more.bookings.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseViewControllerActivity
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.analytics.AnalyticsEvent
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.databinding.ActivityMorePastBookingsDetailBinding
import com.sohohouse.seven.more.bookings.detail.recycler.MorePastBookingsDetailAdapterItem
import com.sohohouse.seven.more.contact.MoreContactActivity
import com.sohohouse.seven.network.core.models.EventBooking

class EventBookingDetailsActivity : BaseViewControllerActivity<EventBookingDetailsPresenter>(),
    EventBookingDetailsViewController, MorePastBookingsDetailContactListener {

    private val binding by viewBinding(ActivityMorePastBookingsDetailBinding::bind)

    override fun createPresenter(): EventBookingDetailsPresenter {
        return App.appComponent.eventBookingDetailsPresenter
    }

    override fun onDataReady(itemList: MutableList<MorePastBookingsDetailAdapterItem>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = MorePastBookingsDetailAdapter(itemList, this)
        binding.recyclerView.adapter = adapter
    }

    override fun getContentLayout(): Int = R.layout.activity_more_past_bookings_detail

    override fun onContactButtonClicked() {
        App.appComponent.analyticsManager.track(AnalyticsEvent.More.Contact)
        val intent = Intent(this, MoreContactActivity::class.java)
        startActivity(intent)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        binding.componentToolbar.toolbarBackBtn.clicks { onBackPressed() }
        val booking = intent.getSerializableExtra(BundleKeys.EVENT_BOOKING) as EventBooking
        presenter.setup(booking, this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun getIntent(context: Context, eventBooking: EventBooking): Intent {
            return Intent(context, EventBookingDetailsActivity::class.java).apply {
                putExtra(BundleKeys.EVENT_BOOKING, eventBooking)
            }
        }
    }
}
