package com.sohohouse.seven.apponboarding.terms

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.databinding.ActivityOnboardingTermsBinding

class OnboardingTermsActivity : BaseMVVMActivity<OnboardingTermsViewModel>(),
    Loadable.View, Errorable.View, OnGlobalLayoutListener {

    override val viewModelClass = OnboardingTermsViewModel::class.java

    override lateinit var loadingView: LoadingView

    override lateinit var errorStateView: ReloadableErrorStateView

    init {
        lifecycleScope.launchWhenStarted {
            viewModel.fetchTerms()
        }
    }

    override fun getContentLayout(): Int = R.layout.activity_onboarding_terms

    private val binding by viewBinding(ActivityOnboardingTermsBinding::bind)

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        with(binding){
            loadingView = activityOnboardingTermsLoadingView
            errorStateView = errorState
        }

        setupViews()
        observeTermsAndConditions()
        observeTermsAccepted()
        observeLoadingState(this) { binding.termsAcceptButton.isEnabled = it == LoadingState.Idle }
        observeErrorState(this) { onClickReload() }
    }

    override fun setBrandingTheme() {
        setTheme(themeManager.darkTheme)
    }

    override fun observeErrorState(owner: LifecycleOwner, onReloadClicked: () -> Unit) {
        super.observeErrorState(owner, onReloadClicked)
        viewModel.error.observe(this) { binding.termsAcceptButton.setInvisible() }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViews() {
        with(binding){
            termsHeader.setText(viewModel.headerResId)
            termsAcceptButton.clicks { viewModel.agreeClicked() }
            bottomSheet.setOnTouchListener { _, _ -> true }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun observeTermsAndConditions() {
        viewModel.termsAndConditions.observe(this) {
            with(binding) {
                termsHeader.setText(viewModel.headerResId)
                termsText.setLinkableHtml(it)
                bottomSheet.setOnTouchListener(null)
            }
        }
    }

    private fun observeTermsAccepted() {
        viewModel.termsAccepted.observe(this) { termsAcceptSucceeded() }
    }

    private fun onClickReload() {
        viewModel.fetchTerms()
        binding.termsAcceptButton.setVisible()
    }

    private fun termsAcceptSucceeded() {
        startActivityAndFinish(viewModel.navigateFrom(this))
    }

    override fun onGlobalLayout() {
        binding.contentView.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

}
