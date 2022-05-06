package com.sohohouse.seven.common.extensions

import android.graphics.drawable.Drawable
import android.text.util.Linkify
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat
import com.sohohouse.seven.common.deeplink.DeeplinkBuilder
import com.sohohouse.seven.common.utils.UrlUtils
import me.saket.bettermovementmethod.BetterLinkMovementMethod

fun TextView.setLinkableHtml(data: String?) {
    if (data.isNullOrEmpty()) {
        this.text = null
        return
    }
    BetterLinkMovementMethod.linkify(Linkify.ALL, this)
        .setOnLinkClickListener { _, url ->
            context.openUrl(
                DeeplinkBuilder.makeDeepLinkable(
                    UrlUtils.sanitiseUrl(url) ?: return@setOnLinkClickListener false
                )
            )
        }
    this.text = HtmlCompat.fromHtml(data, HtmlCompat.FROM_HTML_MODE_LEGACY).trim()
}

fun TextView.setLinkableText(data: String?) {
    if (data.isNullOrEmpty()) {
        this.text = null
        return
    }
    this.text = data
    BetterLinkMovementMethod.linkify(Linkify.ALL, this)
        .setOnLinkClickListener { _, url ->
            context.openUrl(
                DeeplinkBuilder.makeDeepLinkable(
                    UrlUtils.sanitiseUrl(url) ?: return@setOnLinkClickListener false
                )
            )
        }
}

fun TextView.setText(@StringRes text: Int, color: Int) {
    this.text = resources.getString(text)
    setTextColor(color)
}

fun TextView.setText(text: CharSequence, color: Int) {
    this.text = text
    setTextColor(color)
}

fun TextView.toggleVisibiltyIfEmpty() {
    visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
}

fun TextView.setTextOrHide(text: String?) {
    if (text.isNullOrEmpty()) {
        visibility = View.GONE
        return
    }

    this.text = text
    visibility = View.VISIBLE
}

fun TextView.setCompoundDrawableStart(drawable: Drawable?) {
    setCompoundDrawables(
        drawable,
        compoundDrawables[1],
        compoundDrawables[2],
        compoundDrawables[3],
    )
}