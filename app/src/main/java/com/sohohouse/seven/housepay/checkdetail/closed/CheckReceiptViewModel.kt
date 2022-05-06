package com.sohohouse.seven.housepay.checkdetail.closed

import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.error.ErrorHelper
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.housepay.CheckRepo
import com.sohohouse.seven.housepay.checkdetail.receipt.EmailReceiptState
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.models.housepay.Check
import com.sohohouse.seven.network.core.split
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CheckReceiptViewModel @AssistedInject constructor(
    @Assisted private val checkId: String,
    private val checkRepo: CheckRepo,
    private val stringProvider: StringProvider,
    analyticsManager: AnalyticsManager
) : BaseViewModel(analyticsManager), CheckReceiptCallbacks,
    CheckReceiptItemListBuilder by CheckReceiptItemListBuilderImpl(stringProvider) {

    companion object {
        const val FAQS_URL = "https://www.sohohouse.com/en-us/faq/house-pay"
    }

    private val _uiState: MutableStateFlow<CheckReceiptUiState> = MutableStateFlow(
        CheckReceiptUiState.Initial
    )
    val uiState: StateFlow<CheckReceiptUiState> get() = _uiState

    private val _event = MutableSharedFlow<CheckReceiptEvent>()
    val event get() = _event.asSharedFlow()

    private val emailReceiptState: MutableStateFlow<EmailReceiptState> = MutableStateFlow(
        EmailReceiptState.SendCta
    )

    private var check: Check? = null

    init {
        observeEmailReceiptState()
    }

    private fun observeEmailReceiptState() {
        viewModelScope.launch {
            emailReceiptState.collect {
                onEmailReceiptStateChange()
            }
        }
    }

    private fun onEmailReceiptStateChange() {
        if (_uiState.value is CheckReceiptUiState.Receipt) {
            buildAndEmitCheckItems()
        }
    }

    private fun buildAndEmitCheckItems() {
        buildReceiptItems(
            check = check,
            emailReceiptState = emailReceiptState.value,
            callbacks = this
        ).let { items ->
            _uiState.value = CheckReceiptUiState.Receipt(
                items = items,
                updatedAt = System.currentTimeMillis()
            )
        }
    }

    fun refreshCheck() {
        _uiState.value = CheckReceiptUiState.Loading
        viewModelScope.launch(coroutineExceptionHandler) {
            checkRepo.getCheck(checkId).split(
                ifSuccess = {
                    this@CheckReceiptViewModel.check = it
                    onCheckRetrieved()
                }, ifError = {
                    onRetrieveCheckError(it)
                }
            )
        }
    }

    private fun onRetrieveCheckError(it: ApiResponse.Error) {
        _uiState.value = CheckReceiptUiState.ErrorState(
            ErrorHelper.getErrorMessage(
                it.allErrorCodes(),
                stringProvider
            )
        )
    }

    private fun onCheckRetrieved() {
        buildAndEmitCheckItems()
    }

    private fun emailReceipt() {
        emailReceiptState.value = EmailReceiptState.Sending
        viewModelScope.launch(coroutineExceptionHandler) {
            checkRepo.emailReceipt(checkId)
                .split(ifSuccess = {
                    emailReceiptState.value = EmailReceiptState.Sent
                }, ifError = {
                    emailReceiptState.value = EmailReceiptState.SendCta
                })
        }
    }

    override val onPhoneNumberClick: (String) -> Unit = {
        emitEvent(CheckReceiptEvent.CallPhone(it))
    }

    override val onEmailReceiptClick: (EmailReceiptState) -> Unit = {
        if (it == EmailReceiptState.SendCta) {
            emailReceipt()
        }
    }

    override val onFaqsClick: () -> Unit = {
        emitEvent(CheckReceiptEvent.OpenLink(FAQS_URL))
    }

    private fun emitEvent(event: CheckReceiptEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(checkId: String?): CheckReceiptViewModel
    }

}