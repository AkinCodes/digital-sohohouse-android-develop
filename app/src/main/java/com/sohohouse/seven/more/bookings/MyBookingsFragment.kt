package com.sohohouse.seven.more.bookings

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMFragment
import com.sohohouse.seven.databinding.FragmentMyBookingsBinding

@Keep
open class MyBookingsFragment : BaseMVVMFragment<MyBookingsViewModel>(),
    UpcomingBookingsFragment.Listener,
    PastBookingsFragment.Listener {

    override val viewModelClass: Class<MyBookingsViewModel>
        get() = MyBookingsViewModel::class.java

    private val listener get() = activity as? Listener

    override val contentLayoutId get() = R.layout.fragment_my_bookings

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMyBookingsBinding.bind(view)
        with(binding) {
            myBookingsViewpager.adapter = MyBookingsPagerAdapter(childFragmentManager, resources)
            myBookingsViewpager.addOnPageChangeListener(object :
                ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    when (position) {
                        0 -> viewModel.logUpcomingBookingsTabSelected()
                        1 -> viewModel.logPastBookingsTabSelected()
                    }
                }
            })
            myBookingsTablayout.setupWithViewPager(myBookingsViewpager)

            myBookingsToolbar.toolbarTitle.text = getString(R.string.label_my_bookings)
            myBookingsToolbar.toolbarBackBtn.setOnClickListener {
                listener?.onBackPressed()
            }
        }
    }

    override fun onExploreButtonClick() {
        listener?.onExploreEventsClick()
    }

    class MyBookingsPagerAdapter(fm: FragmentManager, val resources: Resources) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(index: Int): Fragment {
            return when (index) {
                0 -> UpcomingBookingsFragment()
                else -> PastBookingsFragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> resources.getString(R.string.upcoming_bookings_header)
                else -> resources.getString(R.string.booking_history_header)
            }
        }

        override fun getCount() = 2
    }

    interface Listener {
        fun onBackPressed() {}
        fun onExploreEventsClick()
    }
}