package com.sohohouse.seven.base.filter

import com.sohohouse.seven.base.error.ErrorViewStateViewController
import com.sohohouse.seven.base.load.LoadViewController
import com.sohohouse.seven.base.mvpimplementation.ViewController

interface BaseFilterViewController : ViewController, LoadViewController,
    ErrorViewStateViewController {
    fun swapFilterType(filterType: FilterType)
    fun resetFilterSelection()
}