package com.sohohouse.seven.base.load

import com.sohohouse.seven.base.mvpimplementation.Presenter
import io.reactivex.SingleTransformer

interface PresenterPaginationLoadable<V : PaginationLoadViewController> : Presenter<V> {
    /**
     * NOT TO USE PUBLICLY.  ONLY FOR SUBCLASSES.
     */
    fun <T> paginationLoadTransformer(): SingleTransformer<T, T> {
        return SingleTransformer { single ->
            single
                .doOnSubscribe {
                    executeWhenAvailable { view, _, _ ->
                        view.loadStarted()
                    }
                }
                .doFinally {
                    executeWhenAvailable { view, _, _ ->
                        view.loadFinished()
                    }
                }
        }
    }
}