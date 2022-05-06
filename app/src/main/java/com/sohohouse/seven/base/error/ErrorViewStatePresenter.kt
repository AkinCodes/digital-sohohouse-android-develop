package com.sohohouse.seven.base.error

import com.sohohouse.seven.base.mvpimplementation.Presenter
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import io.reactivex.SingleTransformer

interface ErrorViewStatePresenter<V : ErrorViewStateViewController> : Presenter<V> {
    fun reloadDataAfterError()
    fun <T> errorViewStateTransformer(): SingleTransformer<Either<ServerError, T>, Either<ServerError, T>> {
        return SingleTransformer { single ->
            return@SingleTransformer single.doAfterSuccess {
                if (it is Either.Error) {
                    executeWhenAvailable { view, _, _ ->
                        view.showReloadableErrorState()
                    }
                }
            }
        }
    }
}