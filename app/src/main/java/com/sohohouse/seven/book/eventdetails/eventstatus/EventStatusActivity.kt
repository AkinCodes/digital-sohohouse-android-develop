package com.sohohouse.seven.book.eventdetails.eventstatus

import android.content.Context
import android.content.Intent
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseViewControllerActivity
import com.sohohouse.seven.book.eventdetails.EventDetailsActivity
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.views.eventdetaillist.BaseEventDetailsAdapterItem
import com.sohohouse.seven.databinding.ActivityEventStatusBinding
import com.sohohouse.seven.main.MainActivity
import com.sohohouse.seven.network.core.models.Event

class EventStatusActivity : BaseViewControllerActivity<EventStatusPresenter>(),
    EventStatusViewController, EventStatusAdapterListener {

    companion object {
        const val NOTIFICATION_EVENT_ID = "id"
        const val NOTIFICATION_EVENT_END_DATE = "eventEndDate"

        fun getIntent(context: Context, item: EventStatusItem): Intent {
            return Intent(context, EventStatusActivity::class.java).apply {
                putExtra(BundleKeys.EVENT_STATUS_ITEM, item)
            }
        }
    }

    private val binding by viewBinding(ActivityEventStatusBinding::bind)

    override fun getContentLayout(): Int = R.layout.activity_event_status

    override fun createPresenter(): EventStatusPresenter {
        return EventStatusPresenter(intent.getSerializableExtra(BundleKeys.EVENT_STATUS_ITEM) as EventStatusItem)
    }

    override fun initLayout(
        event: Event,
        backgroundColor: Int,
        buttonText: String,
        canJoin: Boolean
    ) {

        binding.eventStatusDoneButton.text = buttonText
        binding.eventStatusDoneButton.clicks {
            if (canJoin) {
                startActivity(
                    EventDetailsActivity.getIntent(this, event.id, event.images?.large, true)
                )
                return@clicks
            }

            onBackPressed()
        }

        presenter.setUpData()
    }

    override fun setUpRecyclerView(data: List<BaseEventDetailsAdapterItem>) {
        binding.eventStatusRecyclerview.adapter = EventStatusAdapter(data, this)
    }

    override fun onBackPressed() {
        MainActivity.startClean(this, R.id.menu_book)
    }

    override fun onBackPress() {
        onBackPressed()
    }

}
