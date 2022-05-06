package com.sohohouse.seven.more.bookings

import android.app.Activity
import android.content.Intent
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.InjectableActivity
import com.sohohouse.seven.base.mvvm.LoadingViewController
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.databinding.ActivityMyBookingsBinding

class MyBookingsActivity : InjectableActivity(), MyBookingsFragment.Listener,
    LoadingViewController {

    private val binding by viewBinding(ActivityMyBookingsBinding::bind)

    override val loadingView: LoadingView
        get() = binding.activityMyBookingsLoadingView

    override fun getContentLayout() = R.layout.activity_my_bookings

    override fun onBackPressed() {
        super<MyBookingsFragment.Listener>.onBackPressed()
        finish()
    }

    override fun onExploreEventsClick() {
        val intent = Intent()
        intent.putExtra(MORE_PAST_BOOKINGS_GO_TO_EXPLORE, MORE_PAST_BOOKINGS_GO_TO_EXPLORE)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    companion object {
        const val MORE_PAST_BOOKINGS_ACTION_CODE = 1432
        const val MORE_PAST_BOOKINGS_GO_TO_EXPLORE = "goToExplore!"
    }
}

