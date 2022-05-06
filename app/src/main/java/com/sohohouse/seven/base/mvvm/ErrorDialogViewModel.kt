package com.sohohouse.seven.base.mvvm

import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import io.reactivex.SingleTransformer

interface ErrorDialogViewModel : IViewModel {
    val showNetworkErrorDialogEvent: LiveEvent<Any>
    val showGenericErrorDialogEvent: LiveEvent<Array<out String>>

    fun <T> errorDialogTransformer(): SingleTransformer<Either<ServerError, T>, Either<ServerError, T>>

    fun handleError(error: ServerError?)
}

class ErrorDialogViewModelImpl : ErrorDialogViewModel {

    override val showNetworkErrorDialogEvent: LiveEvent<Any>
        get() = _showNetworkErrorDialogEvent

    override val showGenericErrorDialogEvent: LiveEvent<Array<out String>>
        get() = _showGenericErrorDialogEvent

    private val _showNetworkErrorDialogEvent: LiveEvent<Any> = LiveEvent()

    private val _showGenericErrorDialogEvent: LiveEvent<Array<out String>> = LiveEvent()

    override fun <T> errorDialogTransformer(): SingleTransformer<Either<ServerError, T>, Either<ServerError, T>> {
        return SingleTransformer { single ->
            return@SingleTransformer single.doAfterSuccess {
                when (it) {
                    is Either.Error -> handleError(it.error)
                }
            }
        }
    }

    override fun handleError(error: ServerError?) {
        when (error) {
            ServerError.TIMEOUT, ServerError.NO_INTERNET, ServerError.INVALID_RESPONSE -> {
                _showNetworkErrorDialogEvent.postEvent()
            }
            is ServerError.ApiError -> {
                _showGenericErrorDialogEvent.postValue(error.errorCodes)
            }
            else -> {
                _showGenericErrorDialogEvent.postValue(null)
            }
        }
    }
}