package com.sohohouse.seven.main

import android.content.Context
import android.content.Intent
import com.sohohouse.seven.base.filter.FilterType
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.book.BookTab
import com.sohohouse.seven.common.views.EventType

interface MainNavigationController {

    fun selectExploreTab(bookTab: BookTab)
    fun selectDiscoverTab()
    fun getFilterScreenNavigationIntent(
        context: Context?,
        filterType: FilterType,
        eventType: EventType
    ): Intent

    fun updateProfileImage(imageUrl: String)
    fun indicateCurrentTab(tag: String?)
    fun setLoadingState(state: LoadingState)
    fun setSwipeRefreshLoadingState(state: LoadingState)
}

