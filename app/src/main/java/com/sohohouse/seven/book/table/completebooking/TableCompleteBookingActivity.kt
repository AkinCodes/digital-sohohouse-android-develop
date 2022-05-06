package com.sohohouse.seven.book.table.completebooking

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.book.table.PhoneCode
import com.sohohouse.seven.book.table.TableBookingDetails
import com.sohohouse.seven.book.table.TableBookingUtil
import com.sohohouse.seven.book.table.model.BookedTable
import com.sohohouse.seven.book.table.tableconfirmed.TableConfirmedActivity
import com.sohohouse.seven.common.apihelpers.SohoWebHelper
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.webview.WebViewBottomSheetFragment
import com.sohohouse.seven.common.views.webview.openWebView
import com.sohohouse.seven.databinding.ActivityTableCompleteBookingBinding


class TableCompleteBookingActivity : BaseMVVMActivity<TableCompleteBookingViewModel>(),
    Loadable.View {

    companion object {
        private const val EXTRA_DETAILS = "EXTRA_DETAILS"
        private const val EXTRA_DETAILS_UPDATE = "EXTRA_DETAILS_UPDATE"
        private const val URL_TABLE_TERMS_OF_CONDITION =
            "https://www.opentable.com/legal/terms-and-conditions"
        private const val URL_TABLE_PRIVACY_POLICY =
            "https://www.opentable.com/legal/privacy-policy"

        fun newIntent(context: Context, details: TableBookingDetails): Intent {
            return Intent(context, TableCompleteBookingActivity::class.java).apply {
                this.putExtra(EXTRA_DETAILS, details)
            }
        }

        fun newIntentUpdate(context: Context, details: BookedTable): Intent {
            return Intent(context, TableCompleteBookingActivity::class.java).apply {
                this.putExtra(EXTRA_DETAILS_UPDATE, details)
            }
        }
    }

    override val viewModelClass: Class<TableCompleteBookingViewModel>
        get() = TableCompleteBookingViewModel::class.java

    override val loadingView: LoadingView
        get() = binding.rootLoading

    private val binding by viewBinding(ActivityTableCompleteBookingBinding::bind)

    private val adapter = PhoneCodeAdapter()

    override fun getContentLayout(): Int = R.layout.activity_table_complete_booking

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(binding) {
            countryCode.adapter = adapter
            countryCode.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    phone.text = adapter.getItem(position).dial_code
                }
            }

            close.setOnClickListener { finish() }
            btnBook.setOnClickListener { bookNowClick() }
            termsCheck.setOnCheckedChangeListener { _, isChecked ->
                viewModel.checkTermsOfConditions(
                    isChecked
                )
            }
            confirmationCheck.setOnCheckedChangeListener { _, isChecked ->
                viewModel.checkConfirmation(
                    isChecked
                )
            }
        }
        setUpTermsAndPrivacyPolicy()

        observeLoadingState(this, ::onLoadingStateChange)

        viewModel.phoneCodes.observe(this) { showPhoneNumberForm(it) }
        viewModel.selectedCode.observe(this) { selectCountryCode(it) }
        viewModel.bookingCompleted.observe(this) { gotoBookingCompleteScreen(it) }
        viewModel.updateCompleted.observe(this) { gotoBookingCompleteScreenUpdate(it) }
        viewModel.bookingErrorMessage.observe(this) { showErrorMessage(it) }
        viewModel.enableBookButton.observe(this) { enableBookButton(it) }
        viewModel.phone.observe(this) { showPhone(it) }

        populateData(
            viewModel.init(
                intent.getSerializableExtra(EXTRA_DETAILS) as TableBookingDetails?,
                intent.getSerializableExtra(EXTRA_DETAILS_UPDATE) as BookedTable?
            )
        )
    }

    private fun setUpTermsAndPrivacyPolicy() =
        with(binding) {
            termsOfConditions.movementMethod = LinkMovementMethod.getInstance()
            val termsString = getString(R.string.book_a_table_terms_of_use)
            val privacyPolicyString = getString(R.string.book_a_table_privacy_policy)

            val string = getString(R.string.book_a_table_terms, termsString, privacyPolicyString)
                .createClickableSpannableForSubstring(termsString, {
                    showTermsOfCondition()
                }, theme)
                .createClickableSpannableForSubstring(privacyPolicyString, {
                    showPrivacyPolicy()
                }, theme)

            termsOfConditions.text = string
        }

    private fun showPhoneNumberForm(items: List<PhoneCode>) {
        adapter.refresh(items)
    }

    private fun selectCountryCode(position: Int) {
        binding.countryCode.setSelection(position)
    }

    private fun showPhone(phone: String) {
        with(binding) {
            phoneForm.setGone()
            inputPhone.setGone()
            phoneField.setVisible()
            phoneValue.setVisible()
            phoneValue.text = phone
        }
    }

    private fun enableBookButton(isEnabled: Boolean) {
        binding.btnBook.isEnabled = isEnabled
    }

    private fun showErrorMessage(stringId: Int) {
        TableBookingUtil.createErrorDialog(
            this,
            stringId
        )
        { showContactUs() }
            .show()
    }

    private fun populateData(data: ConfirmationData) =
        with(binding) {
            venueImageAndDetails.apply {
                restaurantTitle.text = data.name
                restaurantAddress.text = data.address
                restaurantCountry.text = data.country
                restaurantImage.setImageFromUrl(data.imageUrl)
            }
            dateTime.text = data.date
            numberOfSeats.text = data.persons
            specialNotesDescription.text = data.specialNotes
            fullName.text = data.username
            email.text = data.email
            additionalComments.setText(data.specialComments)
        }

    private fun showTermsOfCondition() {
        WebViewBottomSheetFragment.withUrl(URL_TABLE_TERMS_OF_CONDITION)
            .show(supportFragmentManager, "ViewMenu")
    }

    private fun showPrivacyPolicy() {
        WebViewBottomSheetFragment.withUrl(URL_TABLE_PRIVACY_POLICY)
            .show(supportFragmentManager, "ViewMenu")
    }

    private fun showContactUs() {
        openWebView(supportFragmentManager, SohoWebHelper.KickoutType.CONTACT_SUPPORT)
    }

    private fun bookNowClick() {
        hideKeyboard()
        with(binding) {
            viewModel.createBooking(
                adapter.getItem(countryCode.selectedItemPosition).code,
                inputPhone.text.toString(),
                additionalComments.text.toString()
            )
        }
    }

    private fun gotoBookingCompleteScreen(details: TableBookingDetails) {
        setResult(Activity.RESULT_OK)
        startActivity(TableConfirmedActivity.newIntent(this, details))
        finish()
    }

    private fun gotoBookingCompleteScreenUpdate(details: BookedTable) {
        startActivity(TableConfirmedActivity.newIntentUpdate(this, details))
        finish()
    }

    private fun onLoadingStateChange(state: LoadingState) {
        binding.btnBook.isEnabled = state != LoadingState.Loading
    }

}