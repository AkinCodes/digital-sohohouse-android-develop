package com.sohohouse.seven.common.analytics

import android.os.Bundle

object NavigationParams {

    private const val NAVIGATION_TAB_TYPE = "tab_type"

    enum class Tab(val value: String) {
        HOME("home"),
        BOOK("book"),
        CONNECT("connect"),
        DISCOVER("discover"),
        ACCOUNT("account")
    }

    fun withTabType(tab: Tab): Bundle {
        return Bundle().apply { putString(NAVIGATION_TAB_TYPE, tab.value) }
    }
}