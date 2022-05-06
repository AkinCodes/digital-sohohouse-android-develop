package com.sohohouse.seven.browsehouses

import com.sohohouse.seven.base.error.ErrorDialogViewController
import com.sohohouse.seven.base.error.ErrorViewStateViewController
import com.sohohouse.seven.base.load.LoadViewController
import com.sohohouse.seven.base.mvpimplementation.ViewController
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem

interface BrowseAllHouseViewController : ViewController, LoadViewController,
    ErrorDialogViewController, ErrorViewStateViewController {
    fun onDataReady(dataItems: List<BaseAdapterItem.BrowseHousesItem>, selectedPosition: Int)
}