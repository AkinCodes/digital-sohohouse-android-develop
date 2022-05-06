package com.sohohouse.seven.housepay.home

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sohohouse.seven.R
import com.sohohouse.seven.profile.view.ProfileViewerAdapter

class HousePayHomePagerAdapter(
    fragment: Fragment,
    val resources: Resources,
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HousePayTabsFragment()
            1 -> HousePayTabsFragment()
            else -> HousePayReceiptsFragment()
        }
    }

    @StringRes
    fun getTitleStringId(position: Int): Int = when (position) {
        0 -> R.string.housepay_tabs_title
        1 -> R.string.housepay_payment_methods_title
        else -> R.string.housepay_receipts_title
    }
}
