package com.sohohouse.seven.memberonboarding.induction.confirmation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseViewControllerActivity
import com.sohohouse.seven.common.dagger.appComponent
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.utils.imageloader.ImageLoader
import com.sohohouse.seven.databinding.ActivityInductionConfirmationBinding
import com.sohohouse.seven.memberonboarding.MemberOnboardingFlowManager
import com.sohohouse.seven.network.core.models.Event
import com.sohohouse.seven.network.core.models.Venue

class InductionConfirmationActivity : BaseViewControllerActivity<InductionConfirmationPresenter>(),
    InductionConfirmationViewController {

    companion object {
        const val EVENT = "introEvent"
        const val VENUE = "venueId"
        const val IS_INDEPENDENT = "isIndependent"
        const val BOOKING_ID = "bookingID"
        const val TRANSITION_NAME_KEY =
            "transitionNameKey" //Necessary to differentiate which image to transition from in the list view
        const val HOUSE_IMAGE = "houseImage"
        const val INDUCTION_BOOKING_REQUEST_CODE = 1446

        fun getFollowUpIntent(context: Context, venue: Venue): Intent {
            val intent = Intent(context, InductionConfirmationActivity::class.java)
            intent.putExtra(VENUE, venue.id)
            return intent
        }

        fun getAppointmentIntent(
            context: Context,
            event: Event,
            venue: Venue,
            bookingID: String,
            isIndependent: Boolean = false,
            houseImageUrl: String? = null
        ): Intent {
            val intent = Intent(context, InductionConfirmationActivity::class.java)
            intent.putExtra(EVENT, event)
            intent.putExtra(VENUE, venue.id)
            intent.putExtra(IS_INDEPENDENT, isIndependent)
            intent.putExtra(BOOKING_ID, bookingID)
            intent.putExtra(HOUSE_IMAGE, houseImageUrl)
            return intent
        }
    }

    private lateinit var imageLoader: ImageLoader

    private lateinit var flowManager: MemberOnboardingFlowManager

    private val houseImageUrl: String by lazy { intent.getStringExtra(HOUSE_IMAGE) ?: "" }

    override fun createPresenter(): InductionConfirmationPresenter {
        val presenter = App.appComponent.inductionConfirmationPresenter
        presenter.venueId = intent.getStringExtra(VENUE) ?: ""
        return presenter
    }

    override fun getContentLayout(): Int = R.layout.activity_induction_confirmation

    private val binding by viewBinding(ActivityInductionConfirmationBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageLoader = App.appComponent.imageLoader
        val userManager = App.appComponent.userManager
        flowManager =
            MemberOnboardingFlowManager(userManager, appComponent.authenticationFlowManager)
        binding.backButton.clicks { onBackPressed() }

        if (houseImageUrl.isNotEmpty()) {
            supportPostponeEnterTransition()
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        with(binding) {
            if (houseImageUrl.isNotEmpty()) {
                imageLoader.load(url = houseImageUrl, isFade = false)
                    .apply { placeholder = R.drawable.placeholder }
                    .into(houseImage, object : ImageLoader.Callback {
                        override fun onSuccess() {
                            supportStartPostponedEnterTransition()
                        }

                        override fun onError() {
                            supportStartPostponedEnterTransition()
                        }
                    })
            } else houseImage.setImageResource(R.drawable.placeholder)

            houseImage.clicks { presenter.doneClicked() }

            bottomButton.clicks { presenter.doneClicked() }
        }

        presenter.fetch()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == INDUCTION_BOOKING_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun setUpLayout(venue: Venue) {
        val event = intent.getSerializableExtra(EVENT) as? Event
        if (event != null) {
            val bookingID = intent.getStringExtra(BOOKING_ID) ?: ""
            val isIndependent = intent.getBooleanExtra(IS_INDEPENDENT, false)
            presenter.loadAppointmentConfirmation(event, bookingID, isIndependent)
        } else {
            presenter.loadFollowUpConfirmation()
        }
    }

    override fun displayAppointment(
        houseName: String,
        houseColor: String,
        houseImage: String,
        dateString: String,
        locationString: String,
        isOffSite: Boolean
    ) {
        binding.houseImage.setImageFromUrl(houseImage)
        with(binding) {
            introHeaderHouseName.text = houseName
            introAppointmentDate.text = dateString
            introAppointmentAddressLine.text = locationString
            introAppointmentDate.setVisible(dateString.isNotEmpty())
            introAppointmentOffsiteEvent.setVisible(isOffSite)
            introAppointmentChange.clicks { presenter.changeClicked() }
            introAppointmentMaps.clicks { presenter.openMapClicked() }
        }
    }

    override fun displayFollowUp(
        houseName: String,
        houseColor: String,
        houseImage: String,
        locationString: String
    ) {
        displayAppointment(houseName, houseColor, houseImage, "", locationString, false)
        with(binding) {
            introAppointmentDateTitle.text = getString(R.string.onboarding_intro_pending_label)
            introAppointmentPendingSupporting.setVisible()
            introAppointmentChange.setGone()
        }
    }

    override fun styleForIndependence(isIndependent: Boolean) {
        with(binding.bottomButton) {
            if (!isIndependent) {
                clicks {
                    presenter.doneClicked()
                }
            }
            setGone()
        }
        binding.contentScrollview.setPadding(0, 0, 0, 0)
    }

    override fun navigateToNextOnboarding() {
        flowManager.navigateAfterConfirmation(this)
    }

    override fun navigateToInductionBooking(bookingID: String) {
        val isIndependent = intent.getBooleanExtra(IS_INDEPENDENT, false)
        val intent = flowManager.navigateBackToInductionBookingActivity(this, bookingID)
        intent.putExtra(IS_INDEPENDENT, isIndependent)
        startActivityForResult(intent, INDUCTION_BOOKING_REQUEST_CODE)
    }

    override fun openLocationInMaps(uriString: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString))
        if (intent.resolveActivity(packageManager) != null) startActivity(intent)
    }

}
