package com.sohohouse.seven.connect

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sohohouse.seven.connect.message.list.MessagesListFragment
import com.sohohouse.seven.connect.mynetwork.connections.ConnectionsFragment
import com.sohohouse.seven.connect.mynetwork.requests.ConnectionRequestsFragment
import com.sohohouse.seven.connect.noticeboard.NoticeboardLandingFragment

class ConnectViewPagerAdapter(
    private val context: Context,
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    lateinit var tabs: MutableList<ConnectTab>

    fun removeTabAndNotifyAdapter(tab: ConnectTab) {
        val index = tabs.indexOf(tab)
        if (index > 0) {
            tabs.remove(tab)
            notifyItemRemoved(index)
        }
    }

    fun addConnectionRequestTab() {
        val index = tabs.lastIndex
        tabs.add(index, ConnectTab.CONNECTION_REQUESTS)
        notifyItemInserted(index)
    }

    fun getPageTitle(position: Int) = context.getString(tabs[position].resId)

    override fun getItemId(position: Int) = tabs[position].id.toLong()

    override fun containsItem(itemId: Long) = tabs.any { it.id.toLong() == itemId }

    override fun getItemCount() = tabs.size

    override fun createFragment(position: Int): Fragment {
        return when (tabs[position]) {
            ConnectTab.NOTICEBOARD -> NoticeboardLandingFragment()
            ConnectTab.MESSAGES -> MessagesListFragment()
            ConnectTab.CONNECTION_REQUESTS -> ConnectionRequestsFragment()
            ConnectTab.MY_CONNECTIONS -> {
                ConnectionsFragment.createInstance(shouldShowBottomButton = true)
            }
        }
    }

}
