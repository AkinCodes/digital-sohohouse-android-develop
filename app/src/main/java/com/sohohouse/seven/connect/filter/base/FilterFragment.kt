package com.sohohouse.seven.connect.filter.base

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.google.android.flexbox.FlexboxLayoutManager
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.extensions.asEnumOrDefault
import com.sohohouse.seven.common.extensions.getParcelableTypedArray
import com.sohohouse.seven.connect.filter.adapter.FilterAdapter
import com.sohohouse.seven.databinding.FragmentRecyclerViewBinding
import timber.log.Timber

abstract class FilterFragment<VM : FilterViewModel> : BaseMVVMFragment<VM>() {

    init {
        lifecycleScope.launchWhenCreated {
            viewModel.mode = filterMode
            viewModel.load(filters)
        }
    }

    private val filterMode: FilterMode
        get() = arguments?.getString(BundleKeys.FILTER_MODE)?.asEnumOrDefault(FilterMode.FILTERS)
            ?: FilterMode.FILTERS

    private val filters: Array<Filter>?
        get() = arguments?.getParcelableTypedArray(BundleKeys.FILTERS)

    private val adapter by lazy { FilterAdapter(filterMode, ::onFilterSelected) }

    protected abstract val filterType: FilterType

    override val contentLayoutId: Int
        get() = R.layout.fragment_recycler_view

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentRecyclerViewBinding.bind(view).bind()
        setupViewModels()
    }

    private fun FragmentRecyclerViewBinding.bind() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = FlexboxLayoutManager(requireContext())
        recyclerView.adapter = adapter

    }

    private fun setupViewModels() {
        viewModel.items.observe(viewLifecycleOwner, {
            adapter.items = it
        })
        viewModel.error.observe(viewLifecycleOwner, {
            // just logging the error for now
            Timber.d(it.toString())
        })
    }

    private fun onFilterSelected(filter: Filter, selected: Boolean) {
        viewModel.setFilterSelected(filter, selected)
    }

    fun getFilters(): List<Filter> = viewModel.getFilters()

    fun resetFilters() = viewModel.resetFilters()

    fun saveFilters() = viewModel.saveFilters()
}
