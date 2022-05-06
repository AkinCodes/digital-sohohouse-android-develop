package com.sohohouse.seven.apponboarding.housepreferences

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.flexbox.FlexboxLayoutManager
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.apponboarding.AppOnboardScreen
import com.sohohouse.seven.apponboarding.AppOnboardingActivity
import com.sohohouse.seven.base.BaseViewControllerFragment
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerChildItem
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerParentItem
import com.sohohouse.seven.databinding.FragmentOnboardingHousePreferencesBinding

interface SelectedLocationListener {
    fun onSelectedLocationsChanged(locations: List<String>)
}

class OnboardingHousePreferencesFragment :
    BaseViewControllerFragment<OnboardingHousePreferencesPresenter>(),
    OnboardingHousePreferencesViewController, SelectedLocationListener {

    val binding by viewBinding(FragmentOnboardingHousePreferencesBinding::bind)

    override fun createPresenter(): OnboardingHousePreferencesPresenter {
        return App.appComponent.onboardingHousePreferencesPresenter
    }

    override val contentLayoutId: Int
        get() = R.layout.fragment_onboarding_house_preferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            housesOnboardingCard.setup(
                R.string.app_onboarding_houses_header,
                R.string.app_onboarding_houses_supporting
            )
            continueButton.clicks { presenter.continueClicked() }
            loadingView = binding.fragmentOnboardingHousePreferencesLoadingView
        }
    }

    override fun onDataReady(
        selectedList: List<LocationRecyclerChildItem>,
        allList: List<LocationRecyclerParentItem>
    ) {
        with(binding.tailoredHouseRecyclerview) {
            layoutManager = FlexboxLayoutManager(context)
            adapter = OnboardingLocationAdapter(
                allList,
                selectedList,
                this@OnboardingHousePreferencesFragment
            )
        }
    }

    override fun updateSuccess() {
        val activity = activity as AppOnboardingActivity
        activity.navigateToNext(AppOnboardScreen.TAILOR)
    }

    override fun onSelectedLocationsChanged(locations: List<String>) {
        presenter.selectedLocationsUpdated(locations)
    }

    override lateinit var loadingView: LoadingView

    override fun showLoadingState() {
        super.showLoadingState()
        binding.continueButton.isEnabled = false
    }

    override fun hideLoadingState() {
        super.hideLoadingState()
        binding.continueButton.isEnabled = true
    }

    override fun getErrorStateView(): ReloadableErrorStateView = binding.errorState

    override fun showReloadableErrorState() {
        super.showReloadableErrorState()
        with(binding) {
            tailoredHouseRecyclerview.setGone()
            errorState.minimizeView()
        }
    }

}
