package com.sohohouse.seven.housepay.payment

import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.FeatureFlags
import com.sohohouse.seven.base.error.DisplayableError
import com.sohohouse.seven.base.error.ErrorHelper
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.models.Card
import com.sohohouse.seven.payment.repo.CardRepo
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChoosePaymentViewModel @AssistedInject constructor(
    analyticsManager: AnalyticsManager,
    private val paymentMethodManager: CheckPaymentMethodManager,
    private val cardRepo: CardRepo,
    private val featureFlags: FeatureFlags,
    private val stringProvider: StringProvider
) : BaseViewModel(
    analyticsManager
) {

    companion object {
        const val PAYMENT_METHOD_CONFIRMED = "PAYMENT_METHOD_CONFIRMED"
    }

    private val _state = MutableStateFlow(UiState.Empty)
    val state: Flow<UiState> get() = _state.asStateFlow()

    private val _event = Channel<Event>(Channel.BUFFERED)
    val event: Flow<Event> = _event.receiveAsFlow()

    private var cards: List<Card> = emptyList()
    private var selectedMethod = paymentMethodManager.selectedPaymentMethod

    private val onPaymentMethodClick: (ChoosePaymentMethodListItem.PaymentMethodListItem) -> Unit =
        {
            selectedMethod = it.paymentMethod
            buildAndEmitListItems()
        }

    private val onAddNewCardClick: () -> Unit = {
        viewModelScope.launch {
            _event.send(Event.OpenAddNewCard)
        }
    }

    init {
        fetchCards(forceRefresh = false)
    }

    private fun fetchCards(forceRefresh: Boolean) {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch(coroutineExceptionHandler) {
            when (val cardsResult = cardRepo.getPaymentMethods(forceRefresh)) {
                is ApiResponse.Success -> {
                    onCards(cardsResult.response)
                }
                is ApiResponse.Error -> {
                    onCardsError(cardsResult)
                }
            }
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private fun onCardsError(error: ApiResponse.Error) {
        _state.value = _state.value.copy(
            error = ErrorHelper
                .getErrorMessage(
                    error.allErrorCodes(),
                    stringProvider
                )
        )
    }

    private fun onCards(cards: List<Card>) {
        this.cards = cards
        buildAndEmitListItems()
    }

    private fun buildAndEmitListItems() {
        val listItems = mutableListOf<ChoosePaymentMethodListItem>().apply {
            addAll(cards.map { card ->
                val isSelected = (selectedMethod as? PaymentMethod.PaymentCard)?.card?.id == card.id
                ChoosePaymentMethodListItem.PaymentMethodListItem(
                    isSelected = isSelected,
                    paymentMethod = PaymentMethod.PaymentCard(card),
                    isDefault = card.isPrimary,
                    onClick = onPaymentMethodClick
                )
            })
            add(ChoosePaymentMethodListItem.AddNewCard(onAddNewCardClick))
        }

        _state.value = _state.value.copy(
            listItems = listItems
        )
    }

    fun onConfirmClick() {
        paymentMethodManager.selectedPaymentMethod = selectedMethod
        viewModelScope.launch {
            _event.send(Event.DismissDialog(PAYMENT_METHOD_CONFIRMED))
        }
    }

    fun onCancelClick() {
        viewModelScope.launch {
            _event.send(Event.DismissDialog())
        }
    }

    fun onNewCardAdded() {
        fetchCards(forceRefresh = true)
    }

    data class UiState(
        val listItems: List<ChoosePaymentMethodListItem>,
        val isLoading: Boolean,
        val error: DisplayableError?
    ) {
        companion object {
            val Empty = UiState(
                listItems = emptyList(),
                isLoading = false,
                error = null
            )
        }
    }

    sealed class Event {
        object OpenAddNewCard : Event()
        data class DismissDialog(val reqKey: String? = null) : Event()
    }

    @AssistedFactory
    abstract class Factory {
        abstract fun create(): ChoosePaymentViewModel
    }

}
