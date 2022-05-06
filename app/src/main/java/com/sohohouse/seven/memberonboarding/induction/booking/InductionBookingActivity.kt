package com.sohohouse.seven.memberonboarding.induction.booking

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseViewControllerActivity
import com.sohohouse.seven.book.eventdetails.bookingsuccess.BookingSuccessActivity
import com.sohohouse.seven.common.dagger.appComponent
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.extensions.throttleClick
import com.sohohouse.seven.common.views.CustomDialogFactory
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.databinding.ActivityInductionBookingBinding
import com.sohohouse.seven.memberonboarding.MemberOnboardingFlowManager
import com.sohohouse.seven.memberonboarding.induction.confirmation.InductionConfirmationActivity.Companion.IS_INDEPENDENT
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.Venue
import java.util.*


class InductionBookingActivity : BaseViewControllerActivity<InductionBookingPresenter>(),
    InductionBookingViewController, IntroductionAdapterListener {
    companion object {
        const val BOOKING_ID = "bookingID"

        fun getIntent(context: Context, bookingID: String? = null): Intent {
            val intent = Intent(context, InductionBookingActivity::class.java)
            bookingID?.let {
                intent.putExtra(BOOKING_ID, it)
            }
            return intent
        }
    }

    lateinit var flowManager: MemberOnboardingFlowManager

    var adapter: IntroductionAdapter? = null

    override fun createPresenter(): InductionBookingPresenter {
        val userManager = App.appComponent.userManager
        flowManager =
            MemberOnboardingFlowManager(userManager, appComponent.authenticationFlowManager)
        return App.appComponent.inductionBookingPresenter
    }

    private val binding by viewBinding(ActivityInductionBookingBinding::bind)

    override fun getContentLayout() = R.layout.activity_induction_booking

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // set booking ID if present in intent, which happens on change of appointment
        presenter.bookingID = intent.getStringExtra(BOOKING_ID)
        presenter.isIndependent = intent.getBooleanExtra(IS_INDEPENDENT, false)
        with(binding) {
            loadingView = activityInductionBookingLoadingView
            onboardingIntroBottomButton.throttleClick { presenter.onActionPress() }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BookingSuccessActivity.BOOKING_SUCCESS_REQUEST_CODE) {
            presenter.onReturnFromConfirmation()
        }
    }

    override fun updateSelectedDate(eventId: String) {
        adapter?.updateSelectedDate(eventId)
    }

    override lateinit var loadingView: LoadingView


    override fun updateBookButtonText(resID: Int, isEnabled: Boolean) {
        with(binding.onboardingIntroBottomButton) {
            text = getString(resID)
            this.isEnabled = isEnabled
        }
    }

    override fun setData(dataList: List<BaseInductItem>) {
        with(binding.onboardingIntroRecycler) {
            layoutManager = LinearLayoutManager(this@InductionBookingActivity)
            adapter = IntroductionAdapter(dataList, this@InductionBookingActivity)
            this.adapter = adapter
        }
    }

    override fun showAppointmentSuccessModal(
        eventId: String,
        eventDate: Date?,
        timeZone: String?,
        imageURL: String?,
        houseName: String,
        houseColor: String
    ) {
        val intent = flowManager.navigateToAppointmentSuccess(
            activity = this,
            eventId = eventId,
            eventName = getString(R.string.onboarding_intro_booked_intro_label),
            eventDate = eventDate,
            timeZone = timeZone,
            eventImageUrl = imageURL,
            houseName = houseName,
            houseColor = houseColor
        )

        startActivityForResult(intent, BookingSuccessActivity.BOOKING_SUCCESS_REQUEST_CODE)
    }

    override fun showFollowupSuccessModal(
        eventId: String,
        imageURL: String?,
        houseName: String,
        houseColor: String
    ) {
        val intent = flowManager.navigateToFollowUpSuccess(
            activity = this,
            eventId = eventId,
            eventName = getString(R.string.onboarding_intro_booked_intro_label),
            eventImageUrl = imageURL,
            houseName = houseName,
            houseColor = houseColor
        )

        startActivityForResult(intent, BookingSuccessActivity.BOOKING_SUCCESS_REQUEST_CODE)
    }

    override fun navigateAfterAppointmentSuccess(
        selectedEvent: Event,
        venue: Venue,
        bookingID: String
    ) {
        val intent =
            flowManager.navigateAfterAppointmentSuccess(this, selectedEvent, venue, bookingID)
        startActivity(intent)
        this.overridePendingTransition(0, 0)
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun navigateAfterFollowUpSuccess(localHouse: Venue) {
        val intent = flowManager.navigateAfterFollowUpSuccess(this, localHouse)
        startActivity(intent)
        this.overridePendingTransition(0, 0)
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun appointmentSelected(eventId: String) {
        presenter.onAppointmentSelected(eventId)
    }

    override fun changeConfirmedAppointmentClicked() {
        presenter.onAppointmentChangeClicked()
    }

    override fun requestFollowupClicked() {
        presenter.onRequestFollowupClicked()
    }

    override fun onBackButtonPressed() {
        onBackPressed()
    }

    override fun showBookingError() {
        CustomDialogFactory.createThemedAlertDialog(
            this,
            getString(R.string.general_error_header),
            getString(R.string.general_error_supporting),
            getString(R.string.general_error_ok_cta)
        ).show()
    }

    override fun getErrorStateView(): ReloadableErrorStateView = binding.errorState

    override fun showReloadableErrorState() {
        super.showReloadableErrorState()
        binding.onboardingIntroBottomButton.setGone()
    }

    override fun hideReloadableErrorState() {
        super.hideReloadableErrorState()
        binding.onboardingIntroBottomButton.setVisible()
    }

}