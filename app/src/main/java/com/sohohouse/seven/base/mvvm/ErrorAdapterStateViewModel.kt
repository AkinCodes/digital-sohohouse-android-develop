package com.sohohouse.seven.base.mvvm

interface ErrorAdapterStateViewModel<Error, Empty> : IViewModel {
    fun reloadDataAfterError()
    val presentAdapterErrorStateEvent: LiveEvent<Error>
    val presentAdapterEmptyStateEvent: LiveEvent<Empty>
}