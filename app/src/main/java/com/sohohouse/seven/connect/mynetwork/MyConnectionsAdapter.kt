package com.sohohouse.seven.connect.mynetwork

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sohohouse.seven.connect.mynetwork.connections.ConnectionsFragment
import com.sohohouse.seven.connect.mynetwork.requests.ConnectionRequestsFragment

class MyConnectionsAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = TAB_SIZE

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            TAB_CONNECTIONS -> ConnectionsFragment()
            TAB_REQUESTS -> ConnectionRequestsFragment()
            else -> throw IndexOutOfBoundsException()
        }
    }

    companion object {
        private const val TAB_SIZE = 2
        const val TAB_CONNECTIONS = 0
        const val TAB_REQUESTS = 1
    }

}