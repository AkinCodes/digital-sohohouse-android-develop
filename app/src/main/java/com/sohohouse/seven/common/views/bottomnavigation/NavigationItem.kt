package com.sohohouse.seven.common.views.bottomnavigation

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.sohohouse.seven.R


class NavigationItem(context: Context, attrs: AttributeSet) {

    val id: Int
    val title: String?
    var selected: Boolean = false
    var icon: Drawable?

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.NavigationItem)

        id = ta.getResourceId(R.styleable.NavigationItem_android_id, 0)
        title = ta.getString(R.styleable.NavigationItem_android_title)
        icon = ta.getDrawable(R.styleable.NavigationItem_android_icon)

        ta.recycle()
    }

}