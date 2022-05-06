package com.sohohouse.seven.base.filter.types

import com.sohohouse.seven.base.mvpimplementation.ViewController

interface FilterBaseViewController : ViewController {
    fun resetSelection()
    fun getTitleRes(): Int
    fun onDataReady()
}