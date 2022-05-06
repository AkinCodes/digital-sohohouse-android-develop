package com.sohohouse.seven.base.mvvm

sealed class LoadingState {
    object Loading : LoadingState()
    object Idle : LoadingState()
}