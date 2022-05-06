package com.sohohouse.seven.profile.edit

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.LiveEvent
import com.sohohouse.seven.base.mvvm.postEvent
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.error
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.Occupation
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject

class EditOccupationViewModel : AutoCompleteViewModel<EditOccupationViewModel.OccupationItem> {

    private val occupationRepository: OccupationsRepository

    private val _showInvalidOccupation = LiveEvent<Any>()
    val showInvalidOccupation: LiveData<Any> get() = _showInvalidOccupation

    private val _occupationConfirmed = LiveEvent<Occupation?>()
    val occupationConfirmed: LiveData<Occupation?> get() = _occupationConfirmed

    override val debounce: Long
        get() = 0L

    @Inject
    constructor(
        occupationRepository: OccupationsRepository,
        analyticsManager: AnalyticsManager
    ) : super(analyticsManager) {
        this.occupationRepository = occupationRepository
        init()
    }

    @VisibleForTesting
    constructor(
        occupationRepository: OccupationsRepository,
        scheduler: Scheduler,
        analyticsManager: AnalyticsManager
    ) : super(analyticsManager, scheduler) {
        this.occupationRepository = occupationRepository
        init()
    }

    private var input: String? = null

    val items: LiveData<List<OccupationItem>> =
        Transformations.switchMap(autoCompleteSuggestions) { suggestions ->
            MutableLiveData<List<OccupationItem>>().apply {
                @Suppress("UNCHECKED_CAST")
                postValue(suggestions)
            }
        }

    private fun init() {
        occupationRepository.prefetchData()
    }

    override fun onTextChange(input: String) {
        super.onTextChange(input)
        this.input = input
    }

    override fun onScreenViewed() {
        setScreenNameInternal(AnalyticsManager.Screens.EditOccupation.name)
    }

    override fun getSuggestions(query: String): Single<Either<ServerError, List<OccupationItem>>> {
        return occupationRepository.getOccupations(query)
            .map {
                if (it is Either.Value) {
                    value(it.value.map { OccupationItem(it) })
                } else {
                    error((it as Either.Error).error)
                }
            }
    }

    fun onUserConfirm() {
        val occupation = getValidOccupation()
        if (input.isNullOrBlank() || occupation != null) {
            _occupationConfirmed.postValue(occupation)
        } else {
            _showInvalidOccupation.postEvent()
        }
    }

    private fun getValidOccupation(): Occupation? {
        return items.value?.firstOrNull { it.value == input }?.occupation
    }

    data class OccupationItem(val occupation: Occupation, var selected: Boolean = false) :
        AutoCompleteSuggestion, DiffItem {
        override val value: String
            get() = occupation.name ?: ""

        override val key: Any?
            get() = occupation.id
    }
}