package com.sohohouse.seven.more.synccalendar

import com.sohohouse.seven.base.mvpimplementation.ViewController

interface SyncCalendarViewController : ViewController {
    fun showToolbar()
    fun showContinueButton()
    fun copyTextToClipboard(label: String, text: String)
    fun showSnackBar()
    fun showTitleView()
    fun navigateToNextOnboardingAcitivty()

}