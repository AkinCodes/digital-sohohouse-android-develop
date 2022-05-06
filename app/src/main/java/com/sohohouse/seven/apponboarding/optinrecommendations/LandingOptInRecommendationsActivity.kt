package com.sohohouse.seven.apponboarding.optinrecommendations

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.base.mvvm.ErrorDialogViewController
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.views.CustomDialogFactory
import com.sohohouse.seven.databinding.LandingOptInRecommendationsActivityBinding
import com.sohohouse.seven.main.MainActivity
import com.sohohouse.seven.profile.edit.EditProfileActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LandingOptInRecommendationsActivity :
    BaseMVVMActivity<LandingOptInRecommendationsViewModel>(),
    ErrorDialogViewController,
    Loadable.View {

    override val viewModelClass: Class<LandingOptInRecommendationsViewModel>
        get() = LandingOptInRecommendationsViewModel::class.java

    override fun getContentLayout(): Int {
        return R.layout.landing_opt_in_recommendations_activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val boundView =
            LandingOptInRecommendationsActivityBinding.bind(findViewById(R.id.landingContent))

        boundView.setUpView()
        observeFlows(boundView)

        viewModel.finish.observe(this) {
            MainActivity.start(this, R.id.menu_home)
            finish()
        }

        observeLoadingState(this) {
            boundView.loadingView.toggleSpinner(it is LoadingState.Loading)
        }

        observeErrorDialogEvents()
        viewModel.setScreenName(name = AnalyticsManager.Screens.OnboardNoticeboard.name)
    }

    private fun observeFlows(boundView: LandingOptInRecommendationsActivityBinding) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.actionButtonsState.collect {
                        handleActionButtonsVisibility(boundView, it)
                    }
                }
                launch {
                    viewModel.selectedPageIndex.collect {
                        boundView.viewPager.currentItem = it
                        boundView.pageIndicator.position = it
                    }
                }
                launch {
                    viewModel.showOptOutDialog.collect {
                        showOptOutDialog(it)
                    }
                }
            }
        }
    }

    private fun LandingOptInRecommendationsActivityBinding.setUpView() {
        viewPager.adapter = LandingOptInPagerAdapter(viewModel.pages)
        viewPager.offscreenPageLimit = 3
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.selectPageFromIndex(position)
            }
        })
        viewPager.isUserInputEnabled = false
        pageIndicator.pageCount = 3

        next.setOnClickListener {
            viewModel.selectNextPage()
        }

        continueBtn.setOnClickListener {
            viewModel.logOnContinue()
            startActivities(
                arrayOf(
                    MainActivity.getIntent(
                        this@LandingOptInRecommendationsActivity,
                        R.id.menu_home
                    ),
                    Intent(
                        this@LandingOptInRecommendationsActivity,
                        EditProfileActivity::class.java
                    )
                )
            )
            finish()
        }
    }

    private fun handleActionButtonsVisibility(
        boundView: LandingOptInRecommendationsActivityBinding,
        it: ActionButtons
    ) {
        boundView.next.isVisible = it is ActionButtons.Next
        boundView.continueBtn.isVisible = it is ActionButtons.Continue
    }

    private fun showOptOutDialog(actions: AlertDialogActions) {
        CustomDialogFactory.createThemedAlertDialog(
            this@LandingOptInRecommendationsActivity,
            title = getString(R.string.opt_out_dialog_title),
            message = getString(R.string.opt_out_dialog_message),
            positiveButtonText = getString(R.string.opt_in),
            negativeButtonText = getString(R.string.yes_opt_out),
            positiveClickListener = { dialogInterface, _ ->
                actions.first()
                dialogInterface.dismiss()
            },
            negativeClickListener = { dialogInterface, _ ->
                actions.second()
                dialogInterface.dismiss()
            }
        ).show()
    }


}
