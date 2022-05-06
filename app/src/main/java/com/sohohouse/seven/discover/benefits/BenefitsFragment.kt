package com.sohohouse.seven.discover.benefits

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.Scrollable
import com.sohohouse.seven.common.behaviors.PullToRefreshBehavior
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setGone
import com.sohohouse.seven.common.renderers.SimpleRenderer
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.databinding.FragmentPerksBinding
import com.sohohouse.seven.databinding.ItemPillListItemBinding
import com.sohohouse.seven.discover.benefits.adapter.PerksAdapter
import com.sohohouse.seven.home.houseboard.RendererDiffAdapter
import com.sohohouse.seven.main.MainNavigationController
import com.sohohouse.seven.perks.details.PerksDetailActivity
import com.sohohouse.seven.perks.filter.BenefitsFilterCityActivity
import com.sohohouse.seven.perks.filter.PerksFilterActivity


class BenefitsFragment : BaseMVVMFragment<BenefitsViewModel>(),
    Loadable.View,
    Errorable.View,
    Scrollable,
    Filterable {

    override val viewModelClass: Class<BenefitsViewModel> = BenefitsViewModel::class.java

    override val viewModel: BenefitsViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            viewModelFactory
        )[viewModelClass]
    }

    override val errorStateView: ReloadableErrorStateView
        get() = binding.errorState

    override val swipeRefreshLayout: SwipeRefreshLayout
        get() = binding.swipeRefreshLayout

    private val benefitsAdapter = PerksAdapter(::onPerksItemClicked)

    private val filtersAdapter =
        RendererDiffAdapter().apply { registerRenderers(CityFilterItemRenderer()) }

    override val contentLayoutId: Int
        get() = R.layout.fragment_perks

    private val binding by viewBinding(FragmentPerksBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            recyclerView.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            recyclerView.adapter = benefitsAdapter

            swipeRefreshLayout.setOnRefreshListener { viewModel.invalidate() }

            val layoutParams = recyclerView.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = layoutParams.behavior as PullToRefreshBehavior
            behavior.dependencyId = R.id.strip_container
            swipeRefreshLayout.setOnChildScrollUpCallback { _, _ -> behavior.topOffset.toFloat() != recyclerView.translationY }

            with(filterLayout) {
                if (viewModel.isFilterByCityEnabled()) {
                    refineBtn.clicks { onFilterClicked() }
                    benefitsFilterStripRv.apply {
                        layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        adapter = filtersAdapter
                    }
                } else {
                    listHeader.setGone()
                    benefitsFilterStripRv.setGone()
                }
            }
        }

        viewModel.benefits.observe(viewLifecycleOwner) { benefitsAdapter.submitList(it) }
        viewModel.filters.observe(viewLifecycleOwner) { filtersAdapter.setItems(it) }
        observeLoadingState(viewLifecycleOwner) {
            if (isVisible && isResumed) {
                (requireActivity() as? MainNavigationController)?.setLoadingState(it)
            }
        }
        observeErrorState(viewLifecycleOwner) { viewModel.invalidate() }
    }

    override fun onResume() {
        viewModel.logView()
        super.onResume()
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (REQ_CODE_FILTER_REGION == requestCode && Activity.RESULT_OK == resultCode) {
            viewModel.invalidate()
            return
        }

        if (requestCode == REQ_CODE_FILTER_CITY && resultCode == Activity.RESULT_OK) {
            viewModel.invalidate()
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun scrollToPosition(position: Int) {
        binding.recyclerView.scrollToPosition(position)
    }

    override fun onFilterClicked() {
        if (viewModel.isFilterByCityEnabled()) {
            startActivityForResult(
                Intent(requireContext(), BenefitsFilterCityActivity::class.java),
                REQ_CODE_FILTER_CITY
            )
        } else {
            startActivityForResult(
                Intent(requireContext(), PerksFilterActivity::class.java),
                REQ_CODE_FILTER_REGION
            )
        }
        activity?.overridePendingTransition(R.anim.bottom_up, R.anim.no_animation)
    }

    private fun onPerksItemClicked(id: String, title: String?, promoCode: String?) {
        viewModel.trackEventPerksItem(id, title, promoCode)
        PerksDetailActivity.start(requireContext(), id)
    }

    companion object {
        private const val REQ_CODE_FILTER_REGION = 12345
        private const val REQ_CODE_FILTER_CITY = 54321

        internal const val TAG = "discover_perks"
    }

}

class CityFilterItemRenderer :
    SimpleRenderer<ActiveCityFilterItem>(ActiveCityFilterItem::class.java) {
    override fun bindViewHolder(item: ActiveCityFilterItem, viewHolder: RecyclerView.ViewHolder) {
        with(ItemPillListItemBinding.bind(viewHolder.itemView)) {
            pillview.label = item.name
            pillview.isActivated = false
            pillview.setActionButton(R.drawable.ic_close)
            pillview.clicks { item.onRemoveClick(item.id) }
        }
    }

    override fun getLayoutResId() = R.layout.item_pill_list_item
}

