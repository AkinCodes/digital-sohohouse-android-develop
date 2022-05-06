package com.sohohouse.seven.more.housepreferences

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.flexbox.FlexboxLayoutManager
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseViewControllerActivity
import com.sohohouse.seven.branding.ThemeManager
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.views.CustomDialogFactory
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerChildItem
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerParentItem
import com.sohohouse.seven.databinding.ActivityMoreHousePreferencesBinding
import javax.inject.Inject

interface SelectedLocationListener {
    fun onSelectedLocationsChanged(selectedList: List<String>)
    fun onRegionToggled(parentItem: LocationRecyclerParentItem)
}

class MoreHousePreferencesActivity : BaseViewControllerActivity<MoreHousePreferencesPresenter>(),
    MoreHousePreferencesViewController,
    SelectedLocationListener,
    Injectable {

    private lateinit var clearButton: MenuItem

    @Inject
    lateinit var themeManager: ThemeManager

    override fun createPresenter(): MoreHousePreferencesPresenter {
        return App.appComponent.moreHousePreferencesPresenter
    }

    override fun getContentLayout() = R.layout.activity_more_house_preferences

    val binding by viewBinding(ActivityMoreHousePreferencesBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_left_arrow)
            setDisplayShowTitleEnabled(false)
        }
    }

    override fun setBrandingTheme() {
        setTheme(themeManager.lightTheme)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        binding.applyButton.clicks { presenter.onApplyButtonClicked() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.house_prefs_actionbar_menu, menu)
        clearButton = menu.findItem(R.id.menu_clear)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_clear) {
            CustomDialogFactory.createThemedAlertDialog(context = this,
                title = getString(R.string.more_house_settings_reset_header),
                message = getString(R.string.more_house_settings_reset_supporting),
                positiveButtonText = getString(R.string.more_house_settings_reset_clear_cta),
                negativeButtonText = getString(R.string.more_house_settings_reset_cancel_cta),
                positiveClickListener = { _, _ -> presenter.resetDefaultSelection() })
                .show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDataReady(
        selectedList: List<LocationRecyclerChildItem>,
        allList: List<LocationRecyclerParentItem>
    ) {
        binding.housesRecyclerView.layoutManager = FlexboxLayoutManager(this)
        val adapter = MoreLocationAdapter(allList, selectedList, this)
        binding.housesRecyclerView.adapter = adapter
    }

    override fun resetSelection(localHouse: String) {
        (binding.housesRecyclerView.adapter as? MoreLocationAdapter)?.resetSelection(
            listOf(
                localHouse
            )
        )
    }

    override fun enableApplyButton(isEnabled: Boolean) {
        binding.applyButton.isEnabled = isEnabled
    }

    override fun enableClearButton(isEnabled: Boolean) {
        if (::clearButton.isInitialized) {
            clearButton.isEnabled = isEnabled
        }
    }

    override fun updateSuccess() {
        finish()
    }

    override val loadingView: LoadingView
        get() = binding.activityMoreHousePreferencesLoadingView

    override fun showLoadingState() {
        super.showLoadingState()
        enableClearButton(false)
    }

    override fun hideLoadingState() {
        super.hideLoadingState()
        enableClearButton(true)
    }

    override fun onSelectedLocationsChanged(selectedList: List<String>) {
        presenter.onSelectedLocationsChanged(selectedList)
    }

    override fun onRegionToggled(parentItem: LocationRecyclerParentItem) {
        presenter.onRegionToggled(parentItem)
    }

    override fun getErrorStateView(): ReloadableErrorStateView {
        return binding.errorState
    }

    override fun showReloadableErrorState() {
        super.showReloadableErrorState()
        binding.applyButton.setGone()
    }

    override fun hideReloadableErrorState() {
        super.hideReloadableErrorState()
        binding.applyButton.setVisible()
    }

}
