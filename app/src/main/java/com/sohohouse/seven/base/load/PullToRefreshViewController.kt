package com.sohohouse.seven.base.load

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

interface PullToRefreshViewController : LoadViewController {

    fun getSwipeRefreshLayout(): SwipeRefreshLayout

    override fun showLoadingState() {
        if (getSwipeRefreshLayout().isRefreshing) return
        super.showLoadingState()
    }

    override fun hideLoadingState() {
        val swipeRefreshLayout = getSwipeRefreshLayout()
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
            return
        }
        super.hideLoadingState()
    }

}