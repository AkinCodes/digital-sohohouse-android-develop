package com.sohohouse.seven.more.notifications

import com.sohohouse.seven.base.error.ErrorDialogViewController
import com.sohohouse.seven.base.error.ErrorViewStateViewController
import com.sohohouse.seven.base.load.LoadViewController
import com.sohohouse.seven.base.mvpimplementation.ViewController
import com.sohohouse.seven.more.notifications.recyclerview.MoreNotificationsAdapterItem

interface MoreNotificationsViewController : ViewController, LoadViewController,
    MoreNotificationsSwitchListener,
    MoreNotificationsButtonListener,
    ErrorDialogViewController, ErrorViewStateViewController {
    fun onDataReady(dataItems: List<MoreNotificationsAdapterItem>)
    fun launchContactActivity()
    fun launchNotificationSettingsActivity()
    fun showNotificationOffAlert(
        key: String,
        listener: NotificationEventsToggleListener,
        default: Boolean
    )
}
