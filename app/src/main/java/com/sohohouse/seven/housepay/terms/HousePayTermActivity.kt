package com.sohohouse.seven.housepay.terms

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setLinkableHtml
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.extensions.startActivityAndFinish
import com.sohohouse.seven.common.utils.collectLatest
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.databinding.ActivityHousePayTermsBinding

class HousePayTermActivity : BaseMVVMActivity<HousePayTermsViewModel>(),
    Loadable.View, Errorable.View, ViewTreeObserver.OnGlobalLayoutListener {

    companion object {
        const val KEY_IS_ONBOARDING = "HousePayTermActivity.KEY_IS_ONBOARDING"
    }

    override val viewModelClass: Class<HousePayTermsViewModel>
        get() = HousePayTermsViewModel::class.java

    override val loadingView: LoadingView
        get() = binding.activityOnboardingTermsLoadingView

    override val errorStateView: ReloadableErrorStateView
        get() = binding.errorState

    private val binding by viewBinding(ActivityHousePayTermsBinding::bind)

    private var isOnboarding = true

    init {
        lifecycleScope.launchWhenStarted {
            viewModel.fetchTerms()
        }
    }

    override fun getContentLayout(): Int = R.layout.activity_house_pay_terms

    override fun setBrandingTheme() {
        setTheme(themeManager.darkTheme)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isOnboarding = intent.extras?.get(KEY_IS_ONBOARDING) as Boolean

        setupViews()
        observeTermsAndConditions()
        observeTermsAccepted()
        observeLoadingState(this) {
            binding.termsAcceptButton.isEnabled = it == LoadingState.Idle
        }
        observeErrorState(this) {
            onClickReload()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViews() {
        binding.termsAcceptButton.clicks {
            viewModel.agreeClicked()
        }
        binding.bottomSheet.setOnTouchListener { _, _ -> true }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun observeTermsAndConditions() {
        viewModel.termsAndConditions.collectLatest(this) {
            with(binding) {
                termsText.setLinkableHtml(it)
                bottomSheet.setOnTouchListener(null)
            }
        }
    }

    private fun observeTermsAccepted() {
        viewModel.termsAccepted.collectLatest(this) {
            termsAcceptSucceeded()
        }
    }

    private fun onClickReload() {
        viewModel.fetchTerms()
        binding.termsAcceptButton.setVisible()
    }

    private fun termsAcceptSucceeded() {
        when {
            isOnboarding -> startActivityAndFinish(
                viewModel.navigateFrom(this)
            )
            else -> finish()
        }
    }

    override fun onGlobalLayout() {
        binding.contentView.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }
}