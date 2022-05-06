package com.sohohouse.seven.base.filter.types.categories

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.flexbox.FlexboxLayoutManager
import com.sohohouse.seven.R
import com.sohohouse.seven.base.filter.FilterListener
import com.sohohouse.seven.base.filter.types.FilterUnitFragment
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.common.utils.collectLatest
import com.sohohouse.seven.common.views.categorylist.CategoryDataItem
import com.sohohouse.seven.databinding.FilterCategoriesFragmentBinding

class FilterCategoriesFragment : BaseMVVMFragment<FilterCategoriesViewModel>(),
    FilterUnitFragment {

    private val viewBinding: FilterCategoriesFragmentBinding by viewBinding(
        FilterCategoriesFragmentBinding::bind
    )

    override val contentLayoutId: Int
        get() = R.layout.filter_categories_fragment

    override val viewModelClass: Class<FilterCategoriesViewModel>
        get() = FilterCategoriesViewModel::class.java

    override fun resetSelection() {
        (viewBinding.categoriesRecyclerView.adapter as? FilterCategoriesAdapter)?.clearSelection()
    }

    override fun getTitleRes() = R.string.explore_events_filter_header

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.eventsFlow.collectLatest(viewLifecycleOwner) { event ->
            when (event) {
                is FilterCategoriesViewModel.UiEvent.SetUpLayout -> with(event) {
                    setUpLayout(selectedItems, allDataItems)
                }
                else -> {}
            }
        }
    }

    override fun onDataReady() {
        viewModel.onDataReady(
            (activity as FilterListener).getSelectedCategories(),
            (activity as FilterListener).getAllCategories()
        )
    }

    fun setUpLayout(selectedItems: List<String>, allDataItems: List<CategoryDataItem>) {
        viewBinding.categoriesRecyclerView.layoutManager = FlexboxLayoutManager(context)

        val adapter = FilterCategoriesAdapter(
            selectedItems.toMutableList(),
            allDataItems,
            activity as FilterListener
        )
        viewBinding.categoriesRecyclerView.adapter = adapter
    }

    companion object {
        const val TAG = "FilterCategoriesFragment"
    }
}