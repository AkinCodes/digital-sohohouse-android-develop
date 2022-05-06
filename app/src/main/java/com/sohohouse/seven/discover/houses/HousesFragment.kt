package com.sohohouse.seven.discover.houses

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.Errorable
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.Scrollable
import com.sohohouse.seven.common.apihelpers.SohoWebHelper.KickoutType.HOUSES
import com.sohohouse.seven.common.views.ItemPaddingDecoration
import com.sohohouse.seven.common.views.ReloadableErrorStateView
import com.sohohouse.seven.common.views.webview.WebViewBottomSheetFragment
import com.sohohouse.seven.databinding.FragmentHousesBinding
import com.sohohouse.seven.discover.houses.adapter.HouseRegionAdapter
import com.sohohouse.seven.main.MainNavigationController


class HousesFragment : BaseMVVMFragment<HousesViewModel>(), Loadable.View, Errorable.View,
    Scrollable {

    override val viewModelClass: Class<HousesViewModel> = HousesViewModel::class.java

    override val viewModel: HousesViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            viewModelFactory
        )[viewModelClass]
    }

    override val errorStateView: ReloadableErrorStateView
        get() = binding.errorState

    override val swipeRefreshLayout: SwipeRefreshLayout
        get() = binding.swipeRefreshLayout

    override val contentLayoutId: Int
        get() = R.layout.fragment_houses

    private val binding by viewBinding(FragmentHousesBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = HouseRegionAdapter(::onHouseItemClicked) {
            (parentFragment as? Listener)?.viewPager?.isUserInputEnabled = it
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            ItemPaddingDecoration(
                RecyclerView.VERTICAL,
                resources.getDimensionPixelOffset(R.dimen.dp_40)
            )
        )

        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.loadHouses() }

        viewModel.houses.observe(viewLifecycleOwner) { adapter.submitList(it) }
        observeLoadingState(viewLifecycleOwner) {
            if (isVisible && isResumed) {
                (requireActivity() as? MainNavigationController)?.setLoadingState(it)
            }
        }
        observeErrorState(viewLifecycleOwner) { viewModel.loadHouses() }
    }

    override fun onResume() {
        super.onResume()
        viewModel.logView()
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }

    private fun onHouseItemClicked(slug: String) {
        WebViewBottomSheetFragment.withKickoutType(type = HOUSES, id = slug)
            .show(requireActivity().supportFragmentManager, WebViewBottomSheetFragment.TAG)
    }

    override fun scrollToPosition(position: Int) {
        binding.recyclerView.scrollToPosition(position)
    }

    companion object {
        internal const val TAG = "discover_houses"
    }

    interface Listener {
        val viewPager: ViewPager2
    }
}
