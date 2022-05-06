package com.sohohouse.seven.common.views.inappnotification

import android.content.Context
import android.view.WindowManager

interface InAppNotification {

    fun showInAppNotification(
        context: Context, item: InAppNotificationAdapterItem,
        primaryClickListener: (() -> Unit)? = null,
        secondaryClickListener: (() -> Unit)? = null
    )

}

class InAppNotificationImpl : InAppNotification {

    override fun showInAppNotification(
        context: Context, item: InAppNotificationAdapterItem,
        primaryClickListener: (() -> Unit)?,
        secondaryClickListener: (() -> Unit)?
    ) {
        val dialog = InAppNotificationDialog(
            context = context,
            item = item,
            primaryClickListener = primaryClickListener,
            secondaryClickListener = secondaryClickListener
        )
        val attribute = dialog.window?.attributes ?: return
        val layoutParams = WindowManager.LayoutParams().apply {
            copyFrom(attribute)
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
        }
        dialog.show()
        dialog.window?.attributes = layoutParams
    }

}