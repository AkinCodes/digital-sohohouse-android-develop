package com.sohohouse.seven.base.mvvm

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.sohohouse.seven.BuildConfig
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.error.ErrorReporter
import com.uxcam.UXCam
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 *
 * @param analyticsManager used for logging current screen name
 * @param dispatcher IO dispatcher for background tasks, use [coroutineExceptionHandler] when making
 * requests
 */
abstract class BaseViewModel(
    protected val analyticsManager: AnalyticsManager,
    //FIXME ViewModel shouldnt have a default dispatcher
    //Dispatchers should be injected into repos/data sources (e.g. API services)
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel(), IViewModel {

    val TAG get() = javaClass.simpleName

    val SCREEN: String = " Screen"

    @Deprecated("Use Coroutine")
    protected val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val _screenNameEvent = LiveEvent<String>()

    private val _trackEvent = LiveEvent<AnalyticsManager.Action>()

    protected val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }

    protected open fun onError(throwable: Throwable) {
        if (BuildConfig.DEBUG) throwable.printStackTrace()
        ErrorReporter.logException(throwable)
    }

    @Deprecated("Use coroutineExceptionHandler instead to handle uncaught ecxeptions")
    protected val viewModelContext: CoroutineContext = dispatcher + coroutineExceptionHandler

    val screenNameEvent: LiveEvent<String>
        get() = _screenNameEvent

    override fun onCleared() {
        compositeDisposable.clear()
    }

    open fun onScreenViewed() {
    }

    protected fun setScreenNameInternal(name: String) {
        _screenNameEvent.postValue(name)
    }

    fun setScreenName(activityName: String = "", name: String) {
        analyticsManager.setScreenName(activityName, name)
        UXCam.tagScreenName(name + SCREEN)
    }

    fun logConnected() {

    }

    fun logDisconnected() {

    }
}

data class ItemChangeEvent(val index: Int, val payload: Any? = null)
