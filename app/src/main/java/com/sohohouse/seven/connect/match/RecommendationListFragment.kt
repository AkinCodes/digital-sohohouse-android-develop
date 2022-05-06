package com.sohohouse.seven.connect.match

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.sohohouse.seven.R
import com.sohohouse.seven.base.error.ErrorDialogHelper.showGenericErrorDialog
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.extensions.setFragmentResult
import com.sohohouse.seven.common.views.preferences.ProfileItem
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.connect.filter.FilterBottomSheetFragment
import com.sohohouse.seven.connect.filter.FilterBottomSheetFragment.Companion.REQUEST_CODE_FILTERS
import com.sohohouse.seven.connect.filter.base.FilterType
import com.sohohouse.seven.databinding.FragmentRecommendationListBinding
import com.sohohouse.seven.profile.view.ProfileViewerFragment

class RecommendationListFragment() : BaseMVVMFragment<RecommendationListViewModel>(),
    Loadable.View {
    override val contentLayoutId: Int
        get() = R.layout.fragment_recommendation_list

    override val viewModelClass: Class<RecommendationListViewModel>
        get() = RecommendationListViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = AllRecommendationAdapter(::onMemberClick)
        val binding = FragmentRecommendationListBinding.bind(view)

        binding.initView(adapter)

        binding.backButton.setOnClickListener {
            setFragmentResult(GO_BACK)
        }

        viewModel.suggestedUsersLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.noMatchFoundGroup.isVisible = it.isEmpty()
        }
        viewModel.openProfile.observe(viewLifecycleOwner) { openProfile(it) }
        viewModel.error.observe(viewLifecycleOwner) { showError() }
    }

    private fun FragmentRecommendationListBinding.initView(adapter: AllRecommendationAdapter) {

        filter.apply {
            onFilterClicked = {
                viewModel.removeFilter(it)
            }
            onRefineClicked = {
                navigateToFilterFragment()
            }
        }

        viewModel.filters.observe(viewLifecycleOwner) { filter.adapter.setItems(it) }

        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(requireContext())

        viewModel.loadingState.observe(viewLifecycleOwner) {
            loadingView.toggleSpinner(it is LoadingState.Loading)
        }

        clearFilters.setOnClickListener {
            viewModel.clearFilters()
        }
    }

    private fun navigateToFilterFragment() {
        RecommendationListFilterBottomSheet.withFilterTypes(
            arrayOf(FilterType.INDUSTRY_FILTER, FilterType.CITY_FILTER, FilterType.TOPIC_FILTER)
        ).apply {
            setTargetFragment(
                this@RecommendationListFragment, REQUEST_CODE_FILTERS
            )
        }.showSafe(parentFragmentManager, FilterBottomSheetFragment.TAG)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }

        when (requestCode) {
            REQUEST_CODE_FILTERS -> viewModel.checkUpdateFilters()
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun onMemberClick(memberID: String) {
        viewModel.getMemberProfile(memberID)
    }

    private fun openProfile(item: ProfileItem) {
        ProfileViewerFragment.withProfile(item)
            .showSafe(parentFragmentManager, ProfileViewerFragment.TAG)
    }

    private fun showError() {
        showGenericErrorDialog(requireContext())
    }

    companion object {
        const val GO_BACK = "GO_BACK"
    }
}