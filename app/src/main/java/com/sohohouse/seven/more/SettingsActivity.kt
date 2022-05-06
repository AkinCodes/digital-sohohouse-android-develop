package com.sohohouse.seven.more

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseFragment
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.branding.AppIconChooserFragment
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.databinding.ActivitySettingsBinding
import com.sohohouse.seven.more.change.password.ChangePasswordFragment
import com.sohohouse.seven.more.notifications.NotificationSettingsFragment
import com.sohohouse.seven.more.privacy.PrivacySettingsFragment
import javax.inject.Inject

class SettingsActivity : BaseMVVMActivity<SettingsActivityViewModel>() {

    private val viewBinding by viewBinding(ActivitySettingsBinding::bind, R.id.settingsContainer)

    override fun getContentLayout(): Int = R.layout.activity_settings

    @Inject
    lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpToolbar()
        setUpTabs()
        viewModel.setScreenName(name= AnalyticsManager.Screens.Settings.name)
    }

    private fun setUpToolbar() {
        setSupportActionBar(viewBinding.toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
    }

    private fun setUpTabs() {
        val tabs = getTabList()

        if (viewModel.shouldShowPrivacyTab) {
            tabs.add(1, PrivacySettingsFragment())
        }

        val adapter = SettingsPagerAdapter(this, tabs)

        viewBinding.settingsViewpager.adapter = adapter

        TabLayoutMediator(viewBinding.settingsTabs, viewBinding.settingsViewpager) { tab, pos ->
            tab.setText(adapter.getTabTitle(pos))
        }.attach()
    }

    private fun getTabList() =
        if (userManager.isStaff || userManager.subscriptionType == SubscriptionType.CONNECT) {
            mutableListOf(
                NotificationSettingsFragment(),
                ChangePasswordFragment()
            )
        } else {
            mutableListOf(
                AppIconChooserFragment(),
                NotificationSettingsFragment(),
                ChangePasswordFragment()
            )
        }

    private inner class SettingsPagerAdapter(
        fragment: FragmentActivity,
        private val tabs: List<BaseFragment>
    ) : FragmentStateAdapter(fragment) {

        override fun getItemCount() = tabs.size

        override fun createFragment(position: Int): Fragment {
            return tabs[position]
        }

        @StringRes
        fun getTabTitle(position: Int): Int {
            return when (tabs[position]) {
                is AppIconChooserFragment -> R.string.appearance
                is NotificationSettingsFragment -> R.string.notifications_label
                is PrivacySettingsFragment -> R.string.privacy
                is ChangePasswordFragment -> R.string.more_password_title
                else -> throw IndexOutOfBoundsException()
            }
        }

    }

    override val viewModelClass: Class<SettingsActivityViewModel>
        get() = SettingsActivityViewModel::class.java
}


