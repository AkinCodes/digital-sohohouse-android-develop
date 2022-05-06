package com.sohohouse.seven.profile.edit.pronouns

import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.common.analytics.AnalyticsManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EditPronounsViewModel @AssistedInject constructor(
    @Assisted val selectedPronouns: MutableList<String>,
    analyticsManager: AnalyticsManager,
) : BaseViewModel(analyticsManager) {

    companion object {
        private const val MAX_PRONOUNS = 4
    }

    private val _items: MutableStateFlow<List<PronounItem>> = MutableStateFlow(emptyList())
    val items: StateFlow<List<PronounItem>> get() = _items

    init {
        emitItems()
    }

    private fun emitItems() {
        _items.value = (GenderPronoun.values().map {
            PronounItem(
                it,
                isSelected = it.name in selectedPronouns
            )
        }.toList())
    }

    fun onPronounSelected(item: PronounItem) {
        if (selectedPronouns.contains(item.pronoun.name)) {
            selectedPronouns.remove(item.pronoun.name)
        } else if (selectedPronouns.size < MAX_PRONOUNS) {
            selectedPronouns.add(item.pronoun.name)
        }
        emitItems()
    }

    fun onConfirmed() {
        analyticsManager.logEventAction(AnalyticsManager.Action.EditProfileGenderPronouns)
    }

    data class PronounItem(
        val pronoun: GenderPronoun,
        val isSelected: Boolean
    ) : DiffItem

    @AssistedFactory
    interface Factory {
        fun create(pronouns: MutableList<String>): EditPronounsViewModel
    }
}
