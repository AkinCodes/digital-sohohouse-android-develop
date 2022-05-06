package com.sohohouse.seven.perks.filter

import android.app.Activity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.flexbox.FlexboxLayoutManager
import com.sohohouse.seven.App
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseViewControllerActivity
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.views.categorylist.CategoryAdapterBaseItem
import com.sohohouse.seven.common.views.categorylist.CategorySelectedListener
import com.sohohouse.seven.databinding.ActivityPerksFilterBinding
import com.sohohouse.seven.perks.filter.adapter.FilterRegionAdapter

class PerksFilterActivity : BaseViewControllerActivity<PerksFilterPresenter>(),
    PerksFilterViewController, CategorySelectedListener {

    override fun createPresenter(): PerksFilterPresenter {
        return App.appComponent.perksFilterPresenter
    }

    override fun getContentLayout(): Int = R.layout.activity_perks_filter

    val binding by viewBinding(ActivityPerksFilterBinding::bind)

    override fun onPostResume() {
        super.onPostResume()

        with(binding) {
            setSupportActionBar(filterRegionToolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_close_white)
                setTitle(R.string.perks_filter_by_label)
            }

            filterRegionButton.setText(R.string.perks_filter_apply_cta)

            filterRegionButton.clicks {
                presenter.onDataFiltered()
                presenter.saveSelectionInfo()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    override fun onDataReady(
        selectedRegions: List<String>,
        allRegionDataItems: MutableList<CategoryAdapterBaseItem>
    ) {
        with(binding) {
            filterRegionRecyclerView.layoutManager = FlexboxLayoutManager(context)

            val adapter =
                FilterRegionAdapter(
                    selectedRegions.toMutableList(),
                    allRegionDataItems,
                    this@PerksFilterActivity
                )
            filterRegionRecyclerView.adapter = adapter
        }
    }

    override fun onCategorySelected(selectedItems: List<String>) {
        presenter.onCategorySelected(selectedItems)
    }
}