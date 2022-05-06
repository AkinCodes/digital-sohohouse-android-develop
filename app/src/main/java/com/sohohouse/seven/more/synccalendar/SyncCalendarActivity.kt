package com.sohohouse.seven.more.synccalendar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseViewControllerActivity
import com.sohohouse.seven.common.dagger.appComponent
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.copyToClipboard
import com.sohohouse.seven.common.extensions.getAttributeColor
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.databinding.ActivitySyncCalendarBinding
import com.sohohouse.seven.memberonboarding.MemberOnboardingFlowManager

class SyncCalendarActivity : BaseViewControllerActivity<SyncCalendarPresenter>(),
    SyncCalendarViewController {

    private val binding by viewBinding(ActivitySyncCalendarBinding::bind)

    override fun createPresenter(): SyncCalendarPresenter {
        return App.appComponent.syncCalendarPresenter
    }

    override fun getContentLayout(): Int = R.layout.activity_sync_calendar

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        val showToolbar = intent.getBooleanExtra(INTENT_EXTRA_KEY_SHOW_TOOLBAR, true)
        val showContinue = intent.getBooleanExtra(INTENT_EXTRA_KEY_SHOW_CONTINUE, false)

        presenter.onIntentDataReceived(showToolbar, showContinue)

        with(binding) {
            linkCta.clicks { presenter.onCopyClicked() }
            linkCtaNext.clicks { presenter.onContinueClicked() }
        }
    }

    override fun showTitleView() {
        binding.titleView.setVisible()
    }

    override fun showToolbar() {
        val myToolbar = findViewById<Toolbar>(R.id.toolbar)
        myToolbar.visibility = View.VISIBLE
        setSupportActionBar(myToolbar)
        supportActionBar?.let {
            it.title = getString(R.string.more_calendar_title)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_left_arrow)
        }
    }

    override fun showContinueButton() {
        binding.linkCtaNext.setVisible()
    }

    override fun copyTextToClipboard(label: String, text: String) {
        text.copyToClipboard(this, label)
    }

    override fun showSnackBar() = with(binding) {
        val snackbar = Snackbar.make(
            linkCta,
            R.string.onboarding_calendar_copy_link_success_label,
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction(R.string.onboarding_calendar_copy_link_dismiss_cta) { snackbar.dismiss() }
        snackbar.setActionTextColor(linkCta.getAttributeColor(R.attr.colorSnackbarDismiss))
        snackbar.show()
    }

    override fun navigateToNextOnboardingAcitivty() {
        val userManager = App.appComponent.userManager
        MemberOnboardingFlowManager(
            userManager,
            appComponent.authenticationFlowManager
        ).navigateCompleteMemberOnboarding(this)
    }

    companion object {

        const val INTENT_EXTRA_KEY_SHOW_TOOLBAR = "intent_extra_key_show_toolbar"
        const val INTENT_EXTRA_KEY_SHOW_CONTINUE = "intent_extra_key_show_continue"

        /**
         * Call this method if you need activity with a toolbar, but without continue button
         */
        fun getIntentForActivityWithToolbar(context: Context?): Intent {
            val intent = Intent(context, SyncCalendarActivity::class.java)
            intent.putExtra(INTENT_EXTRA_KEY_SHOW_TOOLBAR, true)
            intent.putExtra(INTENT_EXTRA_KEY_SHOW_CONTINUE, false)
            return intent
        }

        /**
         * Call this method if you need an activity with no toolbar, but with a continue button
         */
        fun getIntentForActivityWithoutToolbar(context: Context?): Intent {
            val intent = Intent(context, SyncCalendarActivity::class.java)
            intent.putExtra(INTENT_EXTRA_KEY_SHOW_TOOLBAR, false)
            intent.putExtra(INTENT_EXTRA_KEY_SHOW_CONTINUE, true)
            return intent
        }
    }
}
