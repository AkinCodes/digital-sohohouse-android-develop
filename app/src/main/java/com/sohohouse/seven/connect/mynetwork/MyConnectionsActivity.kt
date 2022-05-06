package com.sohohouse.seven.connect.mynetwork

import android.content.Context
import android.content.Intent
import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.extensions.replaceBraces
import com.sohohouse.seven.common.navigation.NavigationTrigger
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.connect.mynetwork.MyConnectionsAdapter.Companion.TAB_CONNECTIONS
import com.sohohouse.seven.connect.mynetwork.MyConnectionsAdapter.Companion.TAB_REQUESTS
import com.sohohouse.seven.connect.mynetwork.blockedprofiles.BlockedProfilesBottomSheet
import com.sohohouse.seven.databinding.FragmentMyConnectionsBinding

class MyConnectionsActivity : BaseMVVMActivity<MyConnectionsViewModel>(), Injectable {

    override val viewModelClass: Class<MyConnectionsViewModel>
        get() = MyConnectionsViewModel::class.java

    val binding by viewBinding(FragmentMyConnectionsBinding::bind)

    override fun getContentLayout(): Int {
        return R.layout.fragment_my_connections
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        with(binding) {
            setupViews()
            setupViewModel()
        }
    }

    private fun FragmentMyConnectionsBinding.setupViews() {
        setupTabLayout()
        blockedUsers.setOnClickListener {
            BlockedProfilesBottomSheet().showSafe(
                supportFragmentManager,
                BlockedProfilesBottomSheet.TAG
            )
        }
        backArrow.setOnClickListener { finish() }
        viewPager.post {
            setDestinationTab()
            setPagerAdapterIndex(intent.extras?.getInt(SCREEN))
        }
    }

    private fun FragmentMyConnectionsBinding.setDestinationTab() {
        when (intent.data?.getQueryParameter(BundleKeys.NAVIGATION_TRIGGER)) {
            NavigationTrigger.MUTUAL_CONNECTION_REQUEST_CREATED.value -> {
                viewPager.currentItem = TAB_REQUESTS
            }
            NavigationTrigger.MUTUAL_CONNECTION_REQUEST_UPDATED.value -> {
                viewPager.currentItem = TAB_CONNECTIONS
            }
        }
    }

    private fun FragmentMyConnectionsBinding.setPagerAdapterIndex(pagerScreenIndex: Int?) {
        if (pagerScreenIndex != null) {
            viewPager.currentItem = pagerScreenIndex
        }
    }

    private fun setupViewModel() {
        viewModel.numOfRequests.observe(lifecycleOwner, {
            binding.tabLayout.getTabAt(TAB_REQUESTS)?.text = getTitle(TAB_REQUESTS)
        })
    }

    private fun FragmentMyConnectionsBinding.setupTabLayout() {
        val adapter = MyConnectionsAdapter(this@MyConnectionsActivity)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 1
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTitle(position)
        }.attach()
    }

    private fun getTitle(position: Int): String {
        return when (position) {
            TAB_CONNECTIONS -> getString(R.string.my_connections_mutual_connections)
            TAB_REQUESTS -> {
                val numberOfRequests = viewModel.numOfRequests.value ?: 0
                if (numberOfRequests == 0) {
                    getString(R.string.my_connections_requests)
                } else {
                    getString(R.string.my_connections_requests_number).replaceBraces("$numberOfRequests")
                }
            }
            else -> throw IndexOutOfBoundsException()
        }
    }

    companion object {
        const val TAG = "connections_activity"
        private const val SCREEN = "SCREEN"

        fun createIntent(context: Context, pagerScreen: Int = TAB_CONNECTIONS): Intent {
            val intent = Intent(context, MyConnectionsActivity::class.java)
            intent.putExtra(SCREEN, pagerScreen)
            return intent
        }
    }
}