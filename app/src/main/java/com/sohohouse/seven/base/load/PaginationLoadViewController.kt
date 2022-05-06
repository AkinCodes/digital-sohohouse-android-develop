package com.sohohouse.seven.base.load

import com.sohohouse.seven.base.mvpimplementation.ViewController

interface PaginationLoadViewController : ViewController {
    fun loadStarted()
    fun loadFinished()
}