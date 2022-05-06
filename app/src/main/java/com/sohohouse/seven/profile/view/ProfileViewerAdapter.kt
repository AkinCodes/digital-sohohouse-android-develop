package com.sohohouse.seven.profile.view

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sohohouse.seven.R
import com.sohohouse.seven.connect.noticeboard.NoticeboardLandingFragment

class ProfileViewerAdapter(
    fragment: Fragment,
    private val profileId: String,
    private val isFriendsSubscriptionType: Boolean
) : FragmentStateAdapter(fragment) {

    val fragments = mutableListOf<Fragment>(ProfileFieldsFragment.withProfileId(profileId)).apply {
        if (!isFriendsSubscriptionType) {
            add(NoticeboardLandingFragment.withProfileId(profileId))
        }
    }

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    @StringRes
    fun getTitle(position: Int): Int {
        return when (position) {
            TAB_PROFILE -> R.string.profile_viewer_profile
            TAB_POSTS -> R.string.profile_viewer_posts
            else -> throw IndexOutOfBoundsException()
        }
    }

    companion object {
        const val TAB_PROFILE = 0
        const val TAB_POSTS = 1
    }

}