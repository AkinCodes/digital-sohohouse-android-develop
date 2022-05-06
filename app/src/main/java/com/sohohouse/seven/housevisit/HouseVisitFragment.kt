package com.sohohouse.seven.housevisit

import android.app.Activity
import android.content.Intent
import androidx.annotation.Keep
import com.sohohouse.seven.R
import com.sohohouse.seven.base.filter.FilterType
import com.sohohouse.seven.book.base.BaseBookTabFragment
import com.sohohouse.seven.common.views.EventType
import com.sohohouse.seven.main.MainNavigationController


/*
    House Visit
 */
@Keep
class HouseVisitFragment : BaseBookTabFragment<HouseVisitViewModel>() {
    companion object {
        private const val FILTER_REQUEST_CODE = 1239
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILTER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            viewModel.invalidate()
        }
    }

    override val viewModelClass: Class<HouseVisitViewModel>
        get() = HouseVisitViewModel::class.java

    //region FilterListener
    override fun onFilterButtonClicked() {
        if (activity is MainNavigationController) {
            viewModel.logFilterClick()
            startActivityForResult(
                (activity as MainNavigationController).getFilterScreenNavigationIntent(
                    context,
                    FilterType.LOCATION,
                    EventType.HOUSE_VISIT
                ), FILTER_REQUEST_CODE
            )
            activity?.overridePendingTransition(R.anim.bottom_up, R.anim.no_animation)
        }
    }
}