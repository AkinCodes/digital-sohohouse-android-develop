package com.sohohouse.seven.apponboarding.data

import android.os.Bundle
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setLinkableHtml
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.extensions.startActivityAndFinish
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.databinding.ActivityOnboardingDataBinding

class OnboardingDataActivity : BaseMVVMActivity<OnboardingDataViewModel>(), Loadable.View {

    override val viewModelClass = OnboardingDataViewModel::class.java

    override fun getContentLayout(): Int = R.layout.activity_onboarding_data

    private val binding by viewBinding(ActivityOnboardingDataBinding::bind)

    init {
        lifecycleScope.launchWhenStarted {
            viewModel.getPrivacyPolicy()
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        loadingView = binding.activityOnboardingDataLoadingView
        setupViews()
        setupViewModel()
    }

    private fun setupViews() {
        setupSupportingText()
        with(binding) {
            allowButton.clicks { didDecideAnalytics(true) }
            optOutButton.clicks { didDecideAnalytics(false) }
        }
    }

    private fun didDecideAnalytics(didConsent: Boolean) {
        viewModel.didDecideAnalytics(this, didConsent)
    }

    private fun setupSupportingText() {
        val text = getString(R.string.app_onboarding_data_supporting)
        val privacyText = getString(R.string.app_onboarding_data_privacy_policy)
        val start = text.indexOf(privacyText)
        val length = start + privacyText.length
        with(binding) {
            description.text = Spannable.Factory.getInstance().newSpannable(text).apply {
                setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        privacyPolicy.setVisible()
                        appBarLayout.setExpanded(false, true)
                    }
                }, start, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            description.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun setupViewModel() {
        viewModel.privacyPolicy.observe(this) { binding.privacyPolicy.setLinkableHtml(it) }
        viewModel.intent.observe(this) { startActivityAndFinish(it) }
        observeLoadingState(this)
    }

    override fun setBrandingTheme() {
        setTheme(themeManager.darkTheme)
    }

    override lateinit var loadingView: LoadingView

}