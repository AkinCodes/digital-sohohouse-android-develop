package com.sohohouse.seven.profile.edit.interests

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.*
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.network.core.models.Interest
import com.sohohouse.seven.profile.edit.pill.SectionItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditInterestsViewModel @Inject constructor(
    analyticsManager: AnalyticsManager,
    dispatcher: CoroutineDispatcher,
    private val interestsRepository: InterestsRepository
) : BaseViewModel(analyticsManager, dispatcher),
    ErrorViewStateViewModel by ErrorViewStateViewModelImpl() {

    companion object {
        const val MIN_INTERESTS = 3
        const val MAX_INTERESTS = 10
    }

    private val _items = MutableLiveData<List<DiffItem>>()
    val items: LiveData<List<DiffItem>> get() = _items

    private val _itemChangeEvent = LiveEvent<Int>()
    val itemChangeEvent: LiveData<Int> get() = _itemChangeEvent

    private val _showSelectionLimitHit = LiveEvent<Any>()
    val showSelectionLimitHit: LiveData<Any> get() = _showSelectionLimitHit

    val confirmButtonEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(items) { value = shouldEnableConfirmBtn() }
        addSource(itemChangeEvent) { value = shouldEnableConfirmBtn() }
    }

    private lateinit var preselectedInterests: List<Interest>

    private val onInterestClick: (interest: InterestItem) -> Unit = { interest ->
        if (!interest.isSelected) {
            if (getSelectedInterests().size < MAX_INTERESTS) {
                interest.isSelected = true
                _itemChangeEvent.value = indexOf(interest)
            } else {
                _showSelectionLimitHit.emitEvent()
            }
        } else {
            interest.isSelected = false
            _itemChangeEvent.value = indexOf(interest)
        }
    }

    fun init(selectedInterests: List<Interest>) {
        this.preselectedInterests = selectedInterests
        fetchInterests(selectedInterests)
    }

    private fun indexOf(interest: InterestItem): Int {
        return items.value?.indexOf(interest) ?: -1
    }

    private fun fetchInterests(selectedInterests: List<Interest>) {
        viewModelScope.launch(viewModelContext) {
            interestsRepository.getAllInterests().fold(ifValue = {
                _items.postValue(buildItems(it, selectedInterests))
            }, ifEmpty = {
            }, ifError = {
                showErrorView()
            })
        }
    }

    private fun buildItems(
        allInterests: List<Interest>,
        selectedInterests: List<Interest>
    ): List<DiffItem> {
        val interestsByCategory: Map<String, List<Interest>> =
            allInterests.groupBy { it.category ?: "" }
        return mutableListOf<DiffItem>().apply {
            interestsByCategory.keys.forEach {
                add(SectionItem(it))
                interestsByCategory[it]?.iterator()?.forEach { interest ->
                    add(
                        InterestItem(
                            interest,
                            isSelected = selectedInterests.contains(interest),
                            onClick = onInterestClick
                        )
                    )
                }
            }
        }
    }

    fun getSelectedInterests(): List<Interest> {
        return items.value?.filterIsInstance(InterestItem::class.java)
            ?.filter { it.isSelected }
            ?.map { it.interest }
            ?: emptyList()
    }

    override fun onScreenViewed() {
        setScreenNameInternal(AnalyticsManager.Screens.EditInterests.name)
    }

    fun onResetClick() {
        items.value?.forEachIndexed { index, item ->
            if (item is InterestItem && item.isSelected) {
                item.isSelected = false
                _itemChangeEvent.value = index
            }
        }
    }

    override fun reloadDataAfterError() {
        fetchInterests(preselectedInterests)
    }

    private fun shouldEnableConfirmBtn() = getSelectedInterests().size >= MIN_INTERESTS

    data class InterestItem(
        val interest: Interest,
        var isSelected: Boolean = false,
        val onClick: (item: InterestItem) -> Unit
    ) : DiffItem {
        val label: String
            get() = interest.name ?: ""

        override val key: Any?
            get() = interest.id
    }
}