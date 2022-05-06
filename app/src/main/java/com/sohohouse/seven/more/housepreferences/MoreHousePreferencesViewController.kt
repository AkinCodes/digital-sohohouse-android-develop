package com.sohohouse.seven.more.housepreferences

import com.sohohouse.seven.base.error.ErrorDialogViewController
import com.sohohouse.seven.base.error.ErrorViewStateViewController
import com.sohohouse.seven.base.load.LoadViewController
import com.sohohouse.seven.base.mvpimplementation.ViewController
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerChildItem
import com.sohohouse.seven.common.views.locationlist.LocationRecyclerParentItem

interface MoreHousePreferencesViewController : ViewController, LoadViewController,
    ErrorDialogViewController, ErrorViewStateViewController {
    fun onDataReady(
        selectedList: List<LocationRecyclerChildItem>,
        allList: List<LocationRecyclerParentItem>
    )

    fun resetSelection(localHouse: String)
    fun enableApplyButton(isEnabled: Boolean)
    fun updateSuccess()
    fun enableClearButton(isEnabled: Boolean)
}
