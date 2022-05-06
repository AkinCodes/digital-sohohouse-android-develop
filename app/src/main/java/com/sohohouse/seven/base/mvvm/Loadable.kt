package com.sohohouse.seven.base.mvvm

import androidx.annotation.RestrictTo
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sohohouse.seven.common.views.LoadingView
import io.reactivex.SingleTransformer

interface Loadable {
    interface ViewModel {
        val loadingState: LiveData<LoadingState>

        @RestrictTo(RestrictTo.Scope.SUBCLASSES)
        fun setLoadingState(state: LoadingState)

        fun setLoading()
        fun setIdle()

        @Deprecated("Use loadingState: LiveData")
        fun <T> loadTransformer(isRefreshing: Boolean = false): SingleTransformer<T, T>
    }

    interface View {

        fun observeLoadingState(
            owner: LifecycleOwner,
            onLoadingStateChanged: (LoadingState) -> Unit = {},
        ) {
            viewModel.loadingState.observe(owner) { state ->
                when (state) {
                    is LoadingState.Loading -> {
                        if (swipeRefreshLayout?.isRefreshing != true) {
                            loadingView?.toggleSpinner(true)
                        }
                    }
                    is LoadingState.Idle -> {
                        if (swipeRefreshLayout?.isRefreshing == true) {
                            swipeRefreshLayout?.isRefreshing = false
                        } else {
                            loadingView?.toggleSpinner(false)
                        }
                    }
                }
                onLoadingStateChanged(state)
            }
        }

        val loadingView: LoadingView?
            get() = null

        val swipeRefreshLayout: SwipeRefreshLayout?
            get() = null

        val viewModel: ViewModel
    }

    class ViewModelImpl : ViewModel {
        private val _loadingState: MutableLiveData<LoadingState> = MutableLiveData()

        override val loadingState: LiveData<LoadingState> = _loadingState

        override fun setLoadingState(state: LoadingState) {
            _loadingState.postValue(state)
        }

        override fun setLoading() {
            _loadingState.postValue(LoadingState.Loading)
        }

        override fun setIdle() {
            _loadingState.postValue(LoadingState.Idle)
        }

        override fun <T> loadTransformer(isRefreshing: Boolean): SingleTransformer<T, T> {
            return SingleTransformer { single ->
                single
                    .doOnSubscribe {
                        _loadingState.postValue(LoadingState.Loading)
                    }
                    .doFinally {
                        _loadingState.postValue(LoadingState.Idle)
                    }
            }
        }
    }
}