package com.sohohouse.seven.book.table.tableconfirmed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.book.table.TableBookingDetails
import com.sohohouse.seven.book.table.model.BookedTable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.common.extensions.setTextOrHide
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.ActivityTableConfirmedBinding

class TableConfirmedActivity : BaseMVVMActivity<TableConfirmedViewModel>() {

    companion object {
        private const val EXTRA_DETAILS = "EXTRA_DETAILS"
        private const val EXTRA_DETAILS_UPDATE = "EXTRA_DETAILS_UPDATE"

        fun newIntent(context: Context, details: TableBookingDetails): Intent {
            return Intent(context, TableConfirmedActivity::class.java).apply {
                this.putExtra(EXTRA_DETAILS, details)
            }
        }

        fun newIntentUpdate(context: Context, details: BookedTable): Intent {
            return Intent(context, TableConfirmedActivity::class.java).apply {
                this.putExtra(EXTRA_DETAILS_UPDATE, details)
            }
        }
    }

    override val viewModelClass: Class<TableConfirmedViewModel>
        get() = TableConfirmedViewModel::class.java

    private val binding by viewBinding(ActivityTableConfirmedBinding::bind)

    override fun getContentLayout(): Int = R.layout.activity_table_confirmed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnDone.setOnClickListener { onDoneClick() }

        populateSummary(
            viewModel.init(
                intent.getSerializableExtra(EXTRA_DETAILS) as TableBookingDetails?,
                intent.getSerializableExtra(EXTRA_DETAILS_UPDATE) as BookedTable?
            )
        )
        viewModel.setScreenName(name= AnalyticsManager.Screens.BookingConfirmation.name)
    }

    private fun onDoneClick() {
        viewModel.done()
        finish()
    }

    private fun populateSummary(summary: SummaryData) =
        with(binding) {
            venueImageAndDetails.apply {
                restaurantTitle.text = summary.name
                restaurantAddress.text = summary.address
                restaurantCountry.text = summary.country
                restaurantImage.setImageFromUrl(summary.imageUrl)
            }
            dateTime.text = summary.date
            commentsLabel.setVisible(summary.comments.isNotEmpty())
            comments.setTextOrHide(summary.comments)
            confirmation.text = summary.confirmationNumber
            numberOfSeats.text = summary.persons
        }
}