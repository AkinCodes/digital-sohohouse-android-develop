package com.sohohouse.seven.branding

import android.content.Context
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.IconType
import com.sohohouse.seven.common.user.AppManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject


class AppIconViewModel @Inject constructor(
    private val appManager: AppManager,
    analyticsManager: AnalyticsManager,
    private val dispatcher: CoroutineDispatcher
) : BaseViewModel(analyticsManager) {

    companion object {
        const val APP_ICON_CHANGE_REQUEST = 10011
    }

    fun updateAppIcon(context: Context, iconType: IconType) {
        saveIconTypeInPrefs(iconType)
        context.startService(Intent(context, AppIconService::class.java).apply {
            action = AppIconService.UPDATE_ICON
            putExtra(BundleKeys.ICON_TYPE, iconType.name)
        })
    }

    private fun saveIconTypeInPrefs(iconType: IconType) {
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            appManager.iconType = iconType
        }
    }

}