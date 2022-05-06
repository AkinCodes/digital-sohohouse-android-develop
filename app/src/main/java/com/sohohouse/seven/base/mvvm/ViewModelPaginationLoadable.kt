package com.sohohouse.seven.base.mvvm

import androidx.annotation.RestrictTo
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

@Deprecated("Use Android Paging library")
interface Paginatable {
    interface ViewModel {
        val pageLoadingState: LiveData<LoadingState>

        @RestrictTo(RestrictTo.Scope.SUBCLASSES)
        fun setPageLoadingState(state: LoadingState)
    }

    interface View {
        val viewModel: ViewModel

        fun showPageLoadingIndicator()
        fun hidePageLoadingIndicator()

        fun observePageLoadingState(
            owner: LifecycleOwner,
            onLoadingStateChanged: (LoadingState) -> Unit = {}
        ) {
            viewModel.pageLoadingState.observe(owner, Observer { state ->
                when (state) {
                    is LoadingState.Loading -> {
                        showPageLoadingIndicator()
                    }
                    is LoadingState.Idle -> {
                        hidePageLoadingIndicator()
                    }
                }
                onLoadingStateChanged(state)
            })
        }
    }

    class ViewModelImpl : ViewModel {
        private val _pageLoadingState: MutableLiveData<LoadingState> = MutableLiveData()

        override val pageLoadingState: LiveData<LoadingState> = _pageLoadingState

        override fun setPageLoadingState(state: LoadingState) {
            _pageLoadingState.postValue(state)
        }

    }
}