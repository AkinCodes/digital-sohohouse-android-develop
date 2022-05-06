package com.sohohouse.seven.base.mvvm

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.BuildConfig
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

//Defines a ViewModel for MVVM design
interface IViewModel {
    //intentionally empty
}

abstract class IViewModelBaseImpl {
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (BuildConfig.DEBUG) throwable.printStackTrace()
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }

    protected val supervisorJob = SupervisorJob()

    protected val viewModelScope =
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler + supervisorJob)

}