package com.sohohouse.seven.common.extensions

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

val RecyclerView.ViewHolder.context: Context get() = itemView.context

val RecyclerView.ViewHolder.resources: Resources get() = itemView.resources

fun RecyclerView.ViewHolder.getString(@StringRes id: Int?): String {
    if (id == -1 || id == null) return ""
    return resources.getString(id)
}

fun RecyclerView.ViewHolder.getString(@StringRes id: Int, vararg args: String): String {
    if (id == -1) return ""
    return resources.getString(id, *args)
}

fun RecyclerView.ViewHolder.getQuantityString(@PluralsRes id: Int, quantity: Int): String {
    if (id == -1) return ""
    return resources.getQuantityString(id, quantity)
}

fun RecyclerView.ViewHolder.getColor(@ColorRes id: Int): Int {
    return ContextCompat.getColor(context, id)
}

fun RecyclerView.ViewHolder.getDimensionPixelSize(@DimenRes id: Int): Int {
    return resources.getDimensionPixelSize(id)
}

fun RecyclerView.ViewHolder.getDrawable(@DrawableRes id: Int?): Drawable? {
    id ?: return null
    return ContextCompat.getDrawable(context, id)
}

fun RecyclerView.ViewHolder.getAttributeColor(@AttrRes resId: Int): Int {
    return context.getAttributeColor(resId)
}