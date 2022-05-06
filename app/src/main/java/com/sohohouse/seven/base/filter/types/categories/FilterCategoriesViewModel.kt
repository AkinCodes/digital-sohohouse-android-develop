package com.sohohouse.seven.base.filter.types.categories

import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.views.categorylist.CategoryDataItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class FilterCategoriesViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    private val ioDispatcher: CoroutineDispatcher,
) : BaseViewModel(analyticsManager) {

    private val _eventsFlow = MutableStateFlow<UiEvent>(UiEvent.Empty)
    val eventsFlow = _eventsFlow.asStateFlow()

    fun onDataReady(selectedItems: List<String>, allDataItems: List<CategoryDataItem>) {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            _eventsFlow.value = UiEvent.SetUpLayout(selectedItems, allDataItems)
        }
    }

    sealed class UiEvent {
        data class SetUpLayout(
            val selectedItems: List<String>,
            val allDataItems: List<CategoryDataItem>
        ) : UiEvent()

        object Empty : UiEvent()
    }
}