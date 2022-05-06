package com.sohohouse.seven.intro.profile

import android.os.Bundle
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.databinding.ActivityPrepopulateProfileBinding
import com.sohohouse.seven.intro.profile.PrepopulateProfileViewModel.UiState.COMPLETED
import com.sohohouse.seven.intro.profile.PrepopulateProfileViewModel.UiState.EDITING

class PrepopulateProfileActivity : BaseMVVMActivity<PrepopulateProfileViewModel>(),
    ErrorDialogViewController,
    Loadable.View, ErrorViewStateViewController {

    companion object {
        private const val NUM_PAGES = 2
    }

    override val viewModelClass: Class<PrepopulateProfileViewModel>
        get() = PrepopulateProfileViewModel::class.java

    override fun getContentLayout() = R.layout.activity_prepopulate_profile

    val binding by viewBinding(ActivityPrepopulateProfileBinding::bind)

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        with(binding) {
            setUpViewPager()

            confirmButton.clicks { viewModel.onProceedBtnClick(this@PrepopulateProfileActivity) }

            viewModel.intent.observe(lifecycleOwner) {
                startActivity(it)
            }

            viewModel.state.observe(lifecycleOwner) {
                pageIndicator.position = it.pagerPos
                viewpager.setCurrentItem(it.pagerPos, true)
                confirmButton.text = getString(it.buttonText)
            }

            observeErrorDialogEvents()
            observeErrorViewEvents()
            observeLoadingState(this@PrepopulateProfileActivity) {
                confirmButton.isEnabled = it != LoadingState.Loading
            }
        }
    }

    private fun ActivityPrepopulateProfileBinding.setUpViewPager() {
        pageIndicator.pageCount = NUM_PAGES
        viewpager.isUserInputEnabled = false
        viewpager.adapter = object : FragmentStateAdapter(this@PrepopulateProfileActivity) {
            override fun getItemCount() = NUM_PAGES
            override fun createFragment(position: Int) = when (position) {
                EDITING.pagerPos -> PrepopulateProfileFormFragment()
                COMPLETED.pagerPos -> PrepopulateProfileCompleteFragment()
                else -> throw IllegalArgumentException("Invalid pager position")
            }
        }
    }

    override fun onBackPressed() {
        if (!viewModel.onBackPress()) {
            super.onBackPressed()
        }
    }

    override val loadingView: LoadingView
        get() = binding.activityPrepopulateProfileLoadingView

    override fun getErrorStateView(): ReloadableErrorStateView = binding.errorView

}