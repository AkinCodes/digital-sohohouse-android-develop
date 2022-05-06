package com.sohohouse.seven.base.error

import com.sohohouse.seven.base.mvpimplementation.Presenter
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import io.reactivex.SingleTransformer

interface ErrorAdapterStatePresenter<V : ErrorAdapterStateViewController> : Presenter<V> {
    fun reloadDataAfterError()
    fun presentAdapterErrorState()
    fun presentAdapterEmptyState()
    fun <T> errorAdapterStateTransformer(): SingleTransformer<Either<ServerError, T>, Either<ServerError, T>> {
        return SingleTransformer { single ->
            return@SingleTransformer single.doAfterSuccess {
                if (it is Either.Error) {
                    executeWhenAvailable { _, _, _ ->
                        presentAdapterErrorState()
                    }
                }
            }
        }
    }
}