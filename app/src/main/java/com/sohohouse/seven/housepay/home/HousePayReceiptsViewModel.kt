package com.sohohouse.seven.housepay.home

import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.network.core.models.housepay.Check
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class HousePayReceiptsViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val ioDispatcher: CoroutineDispatcher,
    private val getReceiptsUseCase: GetReceiptsUseCase,
) : BaseViewModel(analyticsManager), Loadable.ViewModel by Loadable.ViewModelImpl() {

    private val _receiptsStateFLow = MutableStateFlow(emptyMap<CharSequence, List<Check>>())
    val receiptsStateFLow = _receiptsStateFLow.asStateFlow()

    fun getReceipts() {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            setLoading()
            _receiptsStateFLow.value = getReceiptsUseCase()
            setIdle()
        }
    }
}
