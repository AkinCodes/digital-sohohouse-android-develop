package com.sohohouse.seven.common.navigation

import android.content.Intent
import android.net.Uri
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.deeplink.DeeplinkBuilder
import com.sohohouse.seven.home.houseboard.items.NotificationItem
import java.net.URLEncoder

object IntentUtils {

    fun from(notification: NotificationItem): Intent? {
        return from(
            screen = notification.navigationScreen,
            navigationResourceId = notification.navigationResourceId,
            imageUrl = notification.imageUrl,
            notificationTrigger = notification.navigationTrigger
        )
    }

    fun from(
        screen: NavigationScreen?,
        navigationResourceId: String? = null,
        imageUrl: String? = null,
        notificationTrigger: NavigationTrigger? = null
    ): Intent {
        val uri = DeeplinkBuilder.buildUri(
            screen,
            navigationResourceId,
            screen?.value,
            notificationTrigger?.value
        )
        return Intent(Intent.ACTION_VIEW, uri).apply {
            putExtra(BundleKeys.IMAGE_URL, imageUrl)
        }
    }

    fun viewOnMapIntent(address: String?): Intent? {
        return address?.let { it ->
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("geo:0,0?q=" + Uri.encode(it))
            )
        }
    }

    fun dialIntent(phoneNumber: String?): Intent? {
        return phoneNumber?.let { it ->
            Intent(
                Intent.ACTION_DIAL,
                Uri.fromParts("tel", it, null)
            )
        }
    }

    fun openUrlIntent(url: String?): Intent? {
        return url?.let { it ->
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(it)
            )
        }
    }
}