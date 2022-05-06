package com.sohohouse.seven.discover

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.Scrollable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.deeplink.DeeplinkBuilder
import com.sohohouse.seven.common.extensions.addOnTabSelectedListener
import com.sohohouse.seven.databinding.FragmentDiscoverBinding
import com.sohohouse.seven.discover.benefits.BenefitsFragment
import com.sohohouse.seven.discover.benefits.Filterable
import com.sohohouse.seven.discover.housenotes.HouseNotesFragment
import com.sohohouse.seven.discover.houses.HousesFragment
import com.sohohouse.seven.main.MainNavigationController
import javax.inject.Inject

class DiscoverFragment : BaseMVVMFragment<DiscoverViewModel>(), Scrollable,
    HousesFragment.Listener {

    @Inject
    internal lateinit var viewInfo: DiscoverViewInfo

    override val viewModelClass: Class<DiscoverViewModel> = DiscoverViewModel::class.java

    private lateinit var adapter: DiscoverAdapter

    override val contentLayoutId get() = R.layout.fragment_discover

    private val binding by viewBinding(FragmentDiscoverBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()

        viewModel.deeplink.observe(viewLifecycleOwner, Observer { uri ->
            val tag = when (uri?.path?.removePrefix("/") ?: return@Observer) {
                DeeplinkBuilder.PATH_DISCOVER_NOTE -> HouseNotesFragment.TAG
                DeeplinkBuilder.PATH_DISCOVER_HOUSES -> HousesFragment.TAG
                DeeplinkBuilder.PATH_DISCOVER_PERKS -> BenefitsFragment.TAG
                else -> return@Observer
            }
            val pos = adapter.indexOf(tag)
            binding.viewPager.apply {
                post {
                    currentItem = pos
                }
            }
            viewModel.deleteDeeplink()
        })

        if (savedInstanceState != null) {
            (requireActivity() as? MainNavigationController)?.setLoadingState(LoadingState.Idle)
        }

        viewModel.setScreenName(name= AnalyticsManager.Screens.Discover.name)
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as? MainNavigationController)?.indicateCurrentTab(tag)
    }

    override fun scrollToPosition(position: Int) {
        val fragment = adapter.getFragmentAt(binding.viewPager.currentItem)
        if (fragment is Scrollable) {
            fragment.scrollToPosition(position)
        }
    }

    private fun setupViews() = with(binding) {
        adapter = DiscoverAdapter(this@DiscoverFragment, viewInfo.screens)
        viewPager.adapter = adapter

        tabLayout.addOnTabSelectedListener(
            tabReselected = { scrollToPosition(0) },
            tabSelected = {
                filter.visibility = if (it?.position == adapter.indexOf(BenefitsFragment.TAG)
                    && viewModel.isBenefitsCityFilterEnabled().not()
                ) View.VISIBLE else View.GONE
            }
        )

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setText(viewInfo.titles[position])
        }.attach()

        filter.setOnClickListener {
            val fragment = adapter.getFragmentAt(viewPager.currentItem)
            if (fragment is Filterable) {
                fragment.onFilterClicked()
            }
        }
    }

    override val viewPager: ViewPager2
        get() = binding.viewPager

}