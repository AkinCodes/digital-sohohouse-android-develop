package com.sohohouse.seven.base.load

import com.sohohouse.seven.base.mvpimplementation.Presenter
import io.reactivex.SingleTransformer

interface PresenterLoadable<V : LoadViewController> : Presenter<V> {
    /**
     * NOT TO USE PUBLICLY.  ONLY FOR SUBCLASSES.
     */
    fun <T> loadTransformer(): SingleTransformer<T, T> {
        return SingleTransformer { single ->
            single
                .doOnSubscribe {
                    executeWhenAvailable { view, _, _ ->
                        view.showLoadingState()
                    }
                }
                .doFinally {
                    executeWhenAvailable { view, _, _ ->
                        view.hideLoadingState()
                    }
                }
        }
    }
}