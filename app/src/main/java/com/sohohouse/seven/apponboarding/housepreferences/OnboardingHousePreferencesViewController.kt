package com.sohohouse.seven.apponboarding.housepreferences

import com.sohohouse.seven.base.error.ErrorDialogViewController
import com.sohohouse.seven.base.error.ErrorViewStateViewController
import com.sohohouse.seven.base.load.LoadViewController
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerChildItem
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerParentItem

interface OnboardingHousePreferencesViewController : LoadViewController, ErrorDialogViewController,
    ErrorViewStateViewController {
    fun onDataReady(
        selectedList: List<LocationRecyclerChildItem>,
        allList: List<LocationRecyclerParentItem>
    )

    fun updateSuccess()
}
