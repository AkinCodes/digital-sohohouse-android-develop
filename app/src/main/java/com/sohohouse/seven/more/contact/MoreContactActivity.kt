package com.sohohouse.seven.more.contact

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseViewControllerActivity
import com.sohohouse.seven.branding.ThemeManager
import com.sohohouse.seven.common.analytics.AnalyticsEvent
import com.sohohouse.seven.common.apihelpers.SohoWebHelper
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.user.GymMembership
import com.sohohouse.seven.common.views.CustomDialogFactory
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.webview.WebViewBottomSheetFragment
import com.sohohouse.seven.databinding.ActivityMoreContactBinding
import com.sohohouse.seven.more.contact.recycler.BaseEnquiryItem
import com.sohohouse.seven.more.contact.recycler.EnquiryAdapter
import com.sohohouse.seven.more.contact.recycler.EnquiryAdapterListener
import javax.inject.Inject

class MoreContactActivity : BaseViewControllerActivity<MoreContactPresenter>(),
    MoreContactViewController,
    EnquiryAdapterListener,
    Injectable {

    private val binding by viewBinding(ActivityMoreContactBinding::bind)

    lateinit var adapter: EnquiryAdapter

    @Inject
    lateinit var themeManager: ThemeManager

    override fun setBrandingTheme() {
        setTheme(themeManager.lightTheme)
    }

    override fun createPresenter(): MoreContactPresenter {
        return App.appComponent.moreContactPresenter
    }

    override fun getContentLayout(): Int = R.layout.activity_more_contact

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

        private const val INTENT_EXTRA_KEY_INQUIRY_TYPE = "intent_extra_key_inquiry_type"
        private const val INTENT_EXTRA_KEY_GYM_MEMBERSHIP_TYPE =
            "intent_extra_key_gym_membership_type"

        fun getIntentForNewsletterInquiry(context: Context): Intent {
            return Intent(context, MoreContactActivity::class.java)
        }

        fun getIntentForDataDeleteInquiry(context: Context): Intent {
            return Intent(context, MoreContactActivity::class.java)
        }

        fun getIntentForMembershipChangeInquiry(context: Context): Intent {
            val intent = Intent(context, MoreContactActivity::class.java)
            intent.putExtra(INTENT_EXTRA_KEY_INQUIRY_TYPE, InquiryType.MEMBERSHIP_CHANGES_ENQUIRY)
            return intent
        }

        fun getIntentForActiveMembershipSubscription(
            context: Context,
            gymMembership: GymMembership
        ): Intent {
            return Intent(context, MoreContactActivity::class.java).apply {
                putExtra(INTENT_EXTRA_KEY_INQUIRY_TYPE, InquiryType.MEMBERSHIP_ACTIVE_SUBSCRIPTION)
                putExtra(INTENT_EXTRA_KEY_GYM_MEMBERSHIP_TYPE, gymMembership.name)
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        with(binding.componentToolbar) {
            toolbarTitle.text = getString(R.string.more_contact_title)
            toolbarBackBtn.clicks { onBackPressed() }
        }

        val predefinedInquiryType =
            intent.extras?.get(INTENT_EXTRA_KEY_INQUIRY_TYPE) as InquiryType?
        val gymMembership =
            intent.extras?.getString(INTENT_EXTRA_KEY_GYM_MEMBERSHIP_TYPE)?.let { name ->
                GymMembership.get(name)
            } ?: GymMembership.NONE

        predefinedInquiryType?.let {
            presenter.onInquiryTypeReceived(it, gymMembership)
            binding.recycler.scrollToPosition(1)
        }

        binding.submitButton.clicks {
            val selectedEnquiryTypes = adapter.selectedEnquiries.map {
                if (it.key == "") {
                    it.copy(key = getString(it.displayTextRes), apiFieldIndex = it.apiFieldIndex)
                } else it
            }
            presenter.onSubmitClicked(selectedEnquiryTypes, adapter.getText().toString())
        }
    }

    override fun showEnquiryForm(
        dataList: MutableList<BaseEnquiryItem>,
        preselectedEnquiryTypes: MutableList<EnquiryType>,
        isBarredAccount: Boolean
    ) {
        adapter = EnquiryAdapter(dataList, preselectedEnquiryTypes, this, isBarredAccount)
        binding.recycler.adapter = adapter
    }

    override fun loadUrlInWebView(url: String) {
        WebViewBottomSheetFragment.withUrl(url)
            .show(supportFragmentManager, WebViewBottomSheetFragment.TAG)
    }

    override val loadingView: LoadingView
        get() = binding.activityMoreContactLoadingView

    override fun resetEnquiryForm() {
        adapter.resetSelected()
    }

    override fun clearInquiryText() {
        adapter.setText("")
        adapter.updateCanSubmit(false)
    }

    override fun canSubmitUpdated(canSubmit: Boolean) {
        binding.submitButton.isEnabled = canSubmit
    }

    override fun visitFaqClicked() {
        App.appComponent.analyticsManager.track(AnalyticsEvent.More.FAQs)

        WebViewBottomSheetFragment.withKickoutType(
            type = SohoWebHelper.KickoutType.FAQS,
            showHeader = true
        )
            .show(supportFragmentManager, WebViewBottomSheetFragment.TAG)
    }

    override fun contactHouseClicked() {
        presenter.onHouseInfoClicked()
    }

    override fun newItemAdded() {
        if (adapter.itemCount > 1) {
            binding.recycler.smoothScrollToPosition(adapter.itemCount - 1)
        }
    }

    override fun showSubmitSuccessDialog() {
        CustomDialogFactory.createThemedAlertDialog(context = this,
            title = getString(R.string.more_contact_success_header),
            message = getString(R.string.more_contact_success_supporting),
            positiveButtonText = getString(R.string.more_contact_success_cta),
            positiveClickListener = DialogInterface.OnClickListener { _, _ ->
                presenter.onSubmitSuccessDialogDismiss()
            },
            onCancelListener = DialogInterface.OnCancelListener {
                presenter.onSubmitSuccessDialogDismiss()
            }
        ).show()
    }
}
