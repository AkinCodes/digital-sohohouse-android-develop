package com.sohohouse.seven.base.filter

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayout
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.base.mvvm.ErrorViewStateViewController
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.book.filter.BookFilterViewModel
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.views.CustomDialogFactory
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.databinding.ActivityBaseFilterBinding

enum class FilterType {
    LOCATION,
    DATE,
    CATEGORIES
}

abstract class BaseFilterActivity : BaseMVVMActivity<BookFilterViewModel>(),
    Loadable.View, ErrorViewStateViewController {

    override fun getContentLayout(): Int = R.layout.activity_base_filter


    override val viewModelClass: Class<BookFilterViewModel>
        get() = BookFilterViewModel::class.java

    protected lateinit var flowManager: BaseFilterFlowManager

    protected val binding by viewBinding(ActivityBaseFilterBinding::bind)

    abstract fun getFilterBtnText(): Int
    abstract fun getMenuLayoutId(): Int
    abstract fun getFlowManagerForThis(): BaseFilterFlowManager
    abstract fun onSetUpLayoutComplete(fragment: Fragment)

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        val myToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(myToolbar)
        flowManager = getFlowManagerForThis()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.filterButton.clicks {
            viewModel.onDataFiltered()
            viewModel.saveSelectionInfo()
            setResult(Activity.RESULT_OK)
            finish()
        }

        configureHeaderButtonsFilter()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(FILTER_TYPE, viewModel.filterType)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(getMenuLayoutId(), menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.menu_clear -> {
                CustomDialogFactory.createThemedAlertDialog(context = this,
                    title = getString(R.string.explore_events_dialogue_clear_filters_header),
                    message = getString(R.string.explore_events_dialogue_clear_filters_label),
                    positiveButtonText = getString(R.string.explore_events_dialogue_clear_filters_yes_cta),
                    negativeButtonText = getString(R.string.explore_events_dialogue_clear_filters_no_cta),
                    positiveClickListener = { _, _ -> viewModel.resetToDefaultSelection() })
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    open fun configureHeaderButtonsFilter() {
        binding.filterButton.setText(getFilterBtnText())

        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}

            override fun onTabUnselected(p0: TabLayout.Tab?) {}

            override fun onTabSelected(p0: TabLayout.Tab?) {

                when (p0?.tag) {
                    FilterType.LOCATION -> viewModel.updateFilterType(FilterType.LOCATION)
                    FilterType.DATE -> viewModel.updateFilterType(FilterType.DATE)
                    FilterType.CATEGORIES -> viewModel.updateFilterType(FilterType.CATEGORIES)
                }
            }

        })
    }

    fun swapFilterType(filterType: FilterType) {
        val fragment = flowManager.transitionFrom(filterType)

        supportFragmentManager.beginTransaction()
            .replace(R.id.filter_container, fragment, flowManager.currentFragmentTag).commit()
        supportFragmentManager.executePendingTransactions()
        viewModel.fetchSelectedFilterInfo()

        onSetUpLayoutComplete(fragment)
    }

    override val loadingView: LoadingView
        get() = binding.activityBaseFilterLoadingView

    override fun getErrorStateView(): ReloadableErrorStateView = binding.errorState

    override fun showReloadableErrorState() {
        super.showReloadableErrorState()
        binding.filterButton.visibility = View.GONE
    }

    override fun hideReloadableErrorState() {
        super.hideReloadableErrorState()
        binding.filterButton.visibility = View.VISIBLE
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.no_animation, R.anim.bottom_down)
    }

    companion object {
        const val FILTER_TYPE = "FilterType"
    }
}