package com.sohohouse.seven.base.mvvm

import androidx.annotation.RestrictTo
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import io.reactivex.SingleTransformer

interface ErrorViewStateViewModel : IViewModel {

    val errorViewState: LiveEvent<Any>

    fun <T> errorViewStateTransformer(): SingleTransformer<Either<ServerError, T>, Either<ServerError, T>>

    fun reloadDataAfterError() {
        // DO NOTHING HERE. OVERRIDE IN VIEWMODEL IF REQUIRED
    }

    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    fun showErrorView()
}

class ErrorViewStateViewModelImpl : ErrorViewStateViewModel {

    override val errorViewState: LiveEvent<Any>
        get() = _errorViewState

    private val _errorViewState: LiveEvent<Any> = LiveEvent()

    override fun <T> errorViewStateTransformer(): SingleTransformer<Either<ServerError, T>, Either<ServerError, T>> {
        return SingleTransformer { single ->
            return@SingleTransformer single.doAfterSuccess {
                if (it is Either.Error) {
                    _errorViewState.postEvent()
                }
            }
        }
    }

    override fun showErrorView() {
        _errorViewState.postEvent()
    }
}