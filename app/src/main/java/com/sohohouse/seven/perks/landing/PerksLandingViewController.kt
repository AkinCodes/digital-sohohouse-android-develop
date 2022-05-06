package com.sohohouse.seven.perks.landing

import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.error.ErrorViewStateViewController
import com.sohohouse.seven.base.load.LoadViewController
import com.sohohouse.seven.base.load.PaginationLoadViewController
import com.sohohouse.seven.base.mvpimplementation.ViewController

interface PerksLandingViewController : LoadViewController, PaginationLoadViewController,
    ViewController, ErrorViewStateViewController {
    fun onDataReady(data: MutableList<DiffItem>)
    fun showErrorState()
    fun addToEndOfAdapter(value: MutableList<DiffItem>)
    fun startFilterActivity()
}