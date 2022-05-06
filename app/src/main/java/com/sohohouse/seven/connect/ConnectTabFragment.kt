package com.sohohouse.seven.connect

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.addOnTabSelectedListener
import com.sohohouse.seven.common.extensions.getAttributeColor
import com.sohohouse.seven.common.utils.collectLatest
import com.sohohouse.seven.databinding.ConnectTabBinding
import com.sohohouse.seven.databinding.FragmentConnectBinding
import com.sohohouse.seven.main.MainNavigationController

class ConnectTabFragment : BaseMVVMFragment<ConnectTabViewModel>() {

    override val contentLayoutId get() = R.layout.fragment_connect

    override val viewModelClass: Class<ConnectTabViewModel>
        get() = ConnectTabViewModel::class.java

    val binding by viewBinding(FragmentConnectBinding::bind)

    private val pagerAdapter: ConnectViewPagerAdapter by lazy {
        ConnectViewPagerAdapter(requireContext(), this)
    }

    private lateinit var tabs: MutableList<ConnectTab>

    private val tabsNumberIndicatorMap = mutableMapOf<ConnectTab, Int>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            (requireActivity() as? MainNavigationController)?.setLoadingState(LoadingState.Idle)
        }
        binding.initViewPager()
        startObserving()
        viewModel.setScreenName(name = AnalyticsManager.Screens.Connect.name)
    }

    private fun FragmentConnectBinding.initViewPager() {
        tabs = mutableListOf(
            ConnectTab.NOTICEBOARD,
            ConnectTab.MESSAGES,
            ConnectTab.MY_CONNECTIONS
        )
        pagerAdapter.tabs = tabs

        connectViewPager.adapter = pagerAdapter
        connectViewPager.isUserInputEnabled = false

        TabLayoutMediator(
            tabLayout,
            connectViewPager
        ) { tab, pos ->
            tab.text = pagerAdapter.getPageTitle(pos)
        }.attach()

        parentFragmentManager.setFragmentResultListener(
            NAVIGATE_TO_CONNECTIONS,
            this@ConnectTabFragment
        ) { _: String, _: Bundle ->
            connectViewPager.setCurrentItem(tabs.indexOf(ConnectTab.MY_CONNECTIONS), true)
        }

        setUpTabs()
    }

    private fun FragmentConnectBinding.setUpTabs() {
        tabs.forEachIndexed { index, _ ->
            tabLayout.getTabAt(index)?.customView =
                ConnectTabBinding.inflate(layoutInflater).root
        }
        addMarginToLastTab()
        updateTabTextColorsBasedOnSelection()
    }

    private fun startObserving() {
        viewModel.unreadCountLiveData.observe(viewLifecycleOwner) {
            tabsNumberIndicatorMap[ConnectTab.MESSAGES] = it
            binding.updateIndicator(ConnectTab.MESSAGES, it)
        }

        viewModel.totalConnections.observe(viewLifecycleOwner) {
            tabsNumberIndicatorMap[ConnectTab.MY_CONNECTIONS] = it
            binding.updateIndicator(ConnectTab.MY_CONNECTIONS, it)
        }

        observeConnectionRequests()
    }

    private fun observeConnectionRequests() {
        viewModel.numberOfConnectRequests.collectLatest(viewLifecycleOwner) {
            tabsNumberIndicatorMap[ConnectTab.CONNECTION_REQUESTS] = it
            with(binding) {
                if (it == 0 && tabLayout.tabCount > 3) {
                    pagerAdapter.removeTabAndNotifyAdapter(ConnectTab.CONNECTION_REQUESTS)
                    setupTabsAndUpdateAllIndicators()
                } else if (it > 0 && tabLayout.tabCount < 4) {
                    pagerAdapter.addConnectionRequestTab()
                    setupTabsAndUpdateAllIndicators()
                }
                updateIndicator(ConnectTab.CONNECTION_REQUESTS, it)
            }
        }
    }

    private fun FragmentConnectBinding.setupTabsAndUpdateAllIndicators() {
        setUpTabs()
        updateAllIndicators()
    }

    private fun FragmentConnectBinding.updateAllIndicators() {
        tabsNumberIndicatorMap.keys.forEach {
            updateIndicator(it, tabsNumberIndicatorMap[it] ?: 0)
        }
    }

    private fun FragmentConnectBinding.updateIndicator(connectTab: ConnectTab, number: Int) {
        val index = tabs.indexOf(connectTab)
        val customView = tabLayout.getTabAt(index)?.customView ?: return
        ConnectTabBinding.bind(customView).itemCount.text =
            if (number == 0) String() else number.toString()
    }

    private fun FragmentConnectBinding.addMarginToLastTab() {
        try {
            (tabLayout.getChildAt(0) as? ViewGroup)?.let {
                it.getChildAt(it.childCount - 1)?.apply {
                    layoutParams = (layoutParams as? LinearLayout.LayoutParams)?.apply {
                        marginEnd = resources.getDimension(R.dimen.dp_7).toInt()
                    }
                }?.requestLayout()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    private fun FragmentConnectBinding.updateTabTextColorsBasedOnSelection() {
        setSelectedTabAsActive()
        tabLayout.addOnTabSelectedListener {
            for (i in 0 until tabLayout.tabCount) {
                val tab = tabLayout.getTabAt(i) ?: continue
                tab.customView?.apply {
                    val layout = ConnectTabBinding.bind(this)
                    if (tab == it) {
                        setTextColor(layout, R.attr.colorTabLayoutIndicator)
                    } else {
                        setTextColor(layout, R.attr.colorTabLayoutUnselected)
                    }
                }
            }
        }
    }

    private fun FragmentConnectBinding.setSelectedTabAsActive() {
        val selectedTab = tabLayout.getTabAt(tabLayout.selectedTabPosition)
            ?.customView?.run(ConnectTabBinding::bind)

        selectedTab?.let { tab ->
            setTextColor(tab, R.attr.colorTabLayoutIndicator)
        }
    }

    private fun setTextColor(layout: ConnectTabBinding, color: Int) {
        val attrColor = context?.getAttributeColor(color)
        attrColor ?: return
        layout.apply {
            text1.setTextColor(attrColor)
            itemCount.setTextColor(attrColor)
        }
    }

    companion object {
        const val NAVIGATE_TO_CONNECTIONS = "NAVIGATE_TO_CONNECTIONS"
    }
}