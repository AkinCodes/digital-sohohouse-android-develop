package com.sohohouse.seven.common.extensions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.common.deeplink.DeeplinkBuilder
import com.sohohouse.seven.common.utils.StringProviderImpl
import com.sohohouse.seven.common.utils.UrlUtils

fun Context.startActivitySafely(intent: Intent?): Boolean {
    return if (intent?.resolveActivity(packageManager) != null) {
        startActivity(intent)
        true
    } else {
        FirebaseCrashlytics.getInstance().log("Unable to open intent: $intent")
        false
    }
}

fun Context.openUrl(url: String): Boolean {
    val formattedUrl = UrlUtils.sanitiseUrl(url) ?: return false
    val deepLinkableUrl = DeeplinkBuilder.makeDeepLinkable(formattedUrl)
    // will deeplink it or open external app safely
    return startActivitySafely(Intent(Intent.ACTION_VIEW, Uri.parse(deepLinkableUrl)))
}

val Context.stringProvider get() = StringProviderImpl(resources)

fun Context.getAttributeColor(@AttrRes resId: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(resId, typedValue, true)
    return typedValue.data
}

fun Context.hasCameraPermission() = ContextCompat.checkSelfPermission(
    this,
    Manifest.permission.CAMERA
) == PackageManager.PERMISSION_GRANTED