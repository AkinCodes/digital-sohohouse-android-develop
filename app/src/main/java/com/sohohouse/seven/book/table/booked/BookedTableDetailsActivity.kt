package com.sohohouse.seven.book.table.booked

import android.content.Context
import android.content.Intent
import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.activityViewModel
import com.sohohouse.seven.book.table.TableBookingHouseDetailsViewHolder
import com.sohohouse.seven.book.table.TableBookingUtil
import com.sohohouse.seven.book.table.model.BookedTable
import com.sohohouse.seven.book.table.update.UpdateTableBookingActivity
import com.sohohouse.seven.common.apihelpers.SohoWebHelper
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.common.extensions.setTextOrHide
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.views.CustomDialogFactory
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.webview.openWebView
import com.sohohouse.seven.databinding.ActivityBookedTableDetailsBinding
import com.sohohouse.seven.databinding.ItemTableBookingHouseDetailsBinding
import javax.inject.Inject

class BookedTableDetailsActivity : BaseMVVMActivity<BookedTableDetailsViewModel>(), Loadable.View {

    companion object {

        private const val EXTRA_BOOKING_DETAILS_ID = "EXTRA_BOOKING_DETAILS_ID"

        fun newIntent(context: Context?, detailsId: String): Intent {
            return Intent(context, BookedTableDetailsActivity::class.java).apply {
                putExtra(EXTRA_BOOKING_DETAILS_ID, detailsId)
            }
        }
    }

    @Inject
    lateinit var assistedFactory: BookedTableDetailsViewModel.Factory

    override val viewModel: BookedTableDetailsViewModel by activityViewModel {
        val isOpenedFromNotification = intent?.data?.getQueryParameter("id") != null
        val id = intent?.getStringExtra(EXTRA_BOOKING_DETAILS_ID)
            ?: intent?.data?.getQueryParameter("id")
            ?: error("Missing BookingDetails id parameter")

        assistedFactory.create(id, isOpenedFromNotification)
    }

    private val binding by viewBinding(ActivityBookedTableDetailsBinding::bind)

    override val viewModelClass: Class<BookedTableDetailsViewModel>
        get() = BookedTableDetailsViewModel::class.java

    override val loadingView: LoadingView
        get() = binding.rootLoading

    override fun getContentLayout(): Int = R.layout.activity_booked_table_details

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnCancel.setOnClickListener { onCancelClick() }

        observeLoadingState(this)
        viewModel.finish.observe(lifecycleOwner, { if (it) finish() })
        viewModel.error.observe(lifecycleOwner, { showErrorMessage(it) })

        viewModel.details.observe(lifecycleOwner) { populateDetails(it) }
        viewModel.navigateToUpdateBooking.observe(lifecycleOwner) {
            try {
                startActivity(UpdateTableBookingActivity.newIntent(this, it))
                finish()
            } catch (e: Exception) {
                viewModel.logBookedTableDateCrash(it, e)
                showErrorMessage(R.string.error_general)
            }
        }

    }

    private fun populateDetails(details: BookedTable) = with(binding) {
        restaurantTitle.text = details.name
        dateLabel.text = details.bookedTableDate.getFormattedDateTime("")
        additionalTitle.setVisible(details.specialComment.isNotEmpty())
        additionalLabel.setTextOrHide(details.specialComment)
        guestsLabel.text = resources.getQuantityString(
            R.plurals.book_a_table_number_of_seats_value, details.seats, details.seats
        )
        tableImage.setImageFromUrl(details.imageUrl)
        confirmation.text = details.confirmationNumber.toString()
        TableBookingHouseDetailsViewHolder(
            ItemTableBookingHouseDetailsBinding.bind(venueDetails.root)
        ).bind(details.venueDetails)

        btnModify.setOnClickListener { onUpdateClick() }
    }

    private fun onCancelClick() {
        CustomDialogFactory.createThemedAlertDialog(
            context = this,
            title = getString(R.string.book_a_table_booked_cancelation_dialog_title),
            message = getString(R.string.book_a_table_booked_cancelation_dialog_message),
            positiveButtonText = getString(R.string.book_a_table_booked_cancelation_dialog_confirm),
            negativeButtonText = getString(R.string.book_a_table_close_cta),
            positiveClickListener = { _, _ -> viewModel.cancelBooking() },
            negativeClickListener = { _, _ -> }
        ).show()
    }

    private fun onUpdateClick() {
        viewModel.updateBooking()
    }

    private fun showErrorMessage(stringId: Int) {
        TableBookingUtil.createErrorDialog(this, stringId) { showContactUs() }.show()
    }

    private fun showContactUs() {
        openWebView(supportFragmentManager, SohoWebHelper.KickoutType.CONTACT_SUPPORT)
    }

}