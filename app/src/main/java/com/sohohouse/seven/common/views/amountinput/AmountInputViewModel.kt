package com.sohohouse.seven.common.views.amountinput

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.common.analytics.AnalyticsManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class AmountInputViewModel @AssistedInject constructor(
    @Assisted private val stateEvaluator: InputStateEvaluator,
    analyticsManager: AnalyticsManager,
) : BaseViewModel(analyticsManager) {

    private val _state = MutableLiveData<AmountInputState>()
    val state: LiveData<AmountInputState> get() = _state

    val currentAmountCents: Int
        get() = stateEvaluator.amountCents

    init {
        _state.value = stateEvaluator.evaluate(InputOperator.None)
    }

    fun onInput(inputOperator: InputOperator) {
        _state.value = stateEvaluator.evaluate(inputOperator)
    }

    @AssistedFactory
    interface Factory {
        fun create(stateEvaluator: InputStateEvaluator): AmountInputViewModel
    }
}