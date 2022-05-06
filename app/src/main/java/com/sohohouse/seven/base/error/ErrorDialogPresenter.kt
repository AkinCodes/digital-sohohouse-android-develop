package com.sohohouse.seven.base.error

import com.sohohouse.seven.base.mvpimplementation.Presenter
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import io.reactivex.SingleTransformer

interface ErrorDialogPresenter<V : ErrorDialogViewController> : Presenter<V> {
    fun <T> errorDialogTransformer(): SingleTransformer<Either<ServerError, T>, Either<ServerError, T>> {
        return SingleTransformer { single ->
            return@SingleTransformer single.doAfterSuccess {
                if (it is Either.Error<*>) {
                    val error = it.error
                    executeWhenAvailable { view, _, _ ->
                        when (error) {
                            ServerError.TIMEOUT, ServerError.NO_INTERNET, ServerError.INVALID_RESPONSE -> {
                                view.showNetworkErrorDialog()
                            }
                            is ServerError.ApiError -> {
                                view.showGenericErrorDialog(*error.errorCodes)
                            }
                            else -> {
                                view.showGenericErrorDialog()
                            }
                        }
                    }
                }
            }
        }
    }
}