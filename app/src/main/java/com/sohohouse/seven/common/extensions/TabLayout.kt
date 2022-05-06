package com.sohohouse.seven.common.extensions

import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab

inline fun TabLayout.addOnTabSelectedListener(
    crossinline tabReselected: (Tab?) -> Unit = {},
    crossinline tabUnselected: (Tab?) -> Unit = {},
    crossinline tabSelected: (Tab?) -> Unit = {}
) {
    addOnTabSelectedListener(object : OnTabSelectedListener {
        override fun onTabReselected(tab: Tab?) {
            tabReselected(tab)
        }

        override fun onTabUnselected(tab: Tab?) {
            tabUnselected(tab)
        }

        override fun onTabSelected(tab: Tab?) {
            tabSelected(tab)
        }
    })
}