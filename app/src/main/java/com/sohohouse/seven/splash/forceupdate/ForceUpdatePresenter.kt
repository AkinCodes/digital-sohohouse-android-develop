package com.sohohouse.seven.splash.forceupdate

import com.sohohouse.seven.BuildConfig
import com.sohohouse.seven.base.BasePresenter

class ForceUpdatePresenter : BasePresenter<ForceUpdateViewController>() {
    companion object {
        private const val PLAY_STORE_LINK_PREFIX = "https://play.google.com/store/apps/details?id="
    }

    override fun onAttach(
        view: ForceUpdateViewController,
        isFirstAttach: Boolean,
        isRecreated: Boolean
    ) {
        super.onAttach(view, isFirstAttach, isRecreated)

        if (isFirstAttach) {
            view.logout()
        }
    }

    fun onPrimaryButtonClicked() {
        val appId = BuildConfig.APPLICATION_ID
        executeWhenAvailable { view, _, _ -> view.updateApp("$PLAY_STORE_LINK_PREFIX$appId") }
    }
}