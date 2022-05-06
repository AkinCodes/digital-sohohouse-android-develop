package com.sohohouse.seven.profile.edit

import androidx.annotation.CallSuper
import androidx.lifecycle.MutableLiveData
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.ErrorViewStateViewModel
import com.sohohouse.seven.base.mvvm.ErrorViewStateViewModelImpl
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.addTo
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit

abstract class AutoCompleteViewModel<T : AutoCompleteSuggestion>(
    analyticsManager: AnalyticsManager,
    private val scheduler: Scheduler? = null    //optionally pass a test scheduler for unit testing
) : BaseViewModel(analyticsManager), ErrorViewStateViewModel by ErrorViewStateViewModelImpl() {

    companion object {
        const val DEFAULT_INPUT_TO_QUERY_DEBOUNCE_MS = 300L
    }

    private val textChangeSubject: Subject<String> = PublishSubject.create()
    val autoCompleteSuggestions = MutableLiveData<List<T>>()

    open val debounce = DEFAULT_INPUT_TO_QUERY_DEBOUNCE_MS

    init {
        subscribeToTextChangeEvents()
    }

    private fun subscribeToTextChangeEvents() {
        textChangeSubject
            .debounce(debounce, TimeUnit.MILLISECONDS, scheduler ?: Schedulers.computation())
            .switchMapSingle { query ->
                getSuggestions(query).compose(errorViewStateTransformer())
            }
            .subscribe {
                if (it is Either.Value) autoCompleteSuggestions.postValue(it.value ?: emptyList())
            }
            .addTo(compositeDisposable)
    }

    @CallSuper
    open fun onTextChange(input: String) {
        textChangeSubject.onNext(input)
    }

    protected abstract fun getSuggestions(query: String): Single<Either<ServerError, List<T>>>

}

interface AutoCompleteSuggestion : DiffItem {
    val value: String
    override val key: Any?
        get() = value
}