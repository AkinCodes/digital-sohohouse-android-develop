package com.sohohouse.seven.splash.forceupdate

import com.sohohouse.seven.base.mvpimplementation.ViewController

interface ForceUpdateViewController : ViewController {
    fun updateApp(url: String)
    fun logout()
}