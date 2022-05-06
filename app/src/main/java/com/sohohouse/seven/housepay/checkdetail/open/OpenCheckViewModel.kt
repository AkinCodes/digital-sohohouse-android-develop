package com.sohohouse.seven.housepay.checkdetail.open

import androidx.lifecycle.viewModelScope
import com.sohohouse.seven.base.error.ErrorHelper
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.base.mvvm.LoadingState
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.error.ErrorReporter
import com.sohohouse.seven.common.extensions.arrayOfNonNull
import com.sohohouse.seven.common.extensions.editAndEmit
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.housepay.CheckRepo
import com.sohohouse.seven.housepay.HousePayConstants
import com.sohohouse.seven.housepay.checkdetail.CheckItem
import com.sohohouse.seven.housepay.checkdetail.TipOptionUiModel
import com.sohohouse.seven.housepay.checkdetail.open.pay.*
import com.sohohouse.seven.housepay.checkdetail.open.pay.PayCheckError.*
import com.sohohouse.seven.housepay.discounts.DiscountManager
import com.sohohouse.seven.housepay.housecredit.HouseCreditManager
import com.sohohouse.seven.housepay.isClosedOrPaid
import com.sohohouse.seven.housepay.payment.CheckPaymentMethodManager
import com.sohohouse.seven.housepay.tips.CheckTipsManager
import com.sohohouse.seven.housepay.tips.Tip
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.core.ApiResponse
import com.sohohouse.seven.network.core.models.AccountUpdate
import com.sohohouse.seven.network.core.models.housepay.Check
import com.sohohouse.seven.network.core.request.PatchAccountAttributesRequest
import com.sohohouse.seven.network.core.split
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class OpenCheckViewModel @AssistedInject constructor(
    @Assisted private val checkId: String,
    private val checkRepo: CheckRepo,
    private val stringProvider: StringProvider,
    private val paymentMethodManager: CheckPaymentMethodManager,
    private val discountManager: DiscountManager,
    private val tipManager: CheckTipsManager,
    private val houseCreditManager: HouseCreditManager,
    private val payCheck: PayCheck,
    private val userManager: UserManager,
    private val zipRequestsUtil: com.sohohouse.seven.common.utils.ZipRequestsUtil,
    analyticsManager: AnalyticsManager,
    val ioDispatcher: CoroutineDispatcher,
) : BaseViewModel(analyticsManager), OpenCheckCallbacks,
    CheckItemListBuilder by CheckItemListBuilderImpl(
        stringProvider, houseCreditManager, tipManager, discountManager
    ) {

    private val _uiState = MutableStateFlow<OpenCheckUiState>(OpenCheckUiState.Initial)
    val uiState: StateFlow<OpenCheckUiState>
        get() = _uiState

    private val _event = MutableSharedFlow<OpenCheckEvent>()
    val event: Flow<OpenCheckEvent>
        get() = _event.asSharedFlow()

    private var applyDiscountError: ApiResponse.Error? = null
    private var fetchHouseCreditError: ApiResponse.Error? = null
    private var payCheckError: ApiResponse.Error? = null

    private var check: Check? = null
        set(value) {
            field = value
            value?.let {
                discountManager.useCheck(it)
                tipManager.useCheck(it)
                houseCreditManager.useCheck(it)
            }
        }

    private var checkState: CheckState = CheckState.Undetermined

    init {
        _uiState.value = OpenCheckUiState.Initial
        viewModelScope.launch(coroutineExceptionHandler) {
            paymentMethodManager.fetchPaymentMethod(forceRefresh = true)   //refresh cards
        }
    }

    private fun retrieveCheck() {
        _uiState.value = OpenCheckUiState.Loading
        doRetrieveCheck(checkId)
    }

    private fun doRetrieveCheck(checkId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            checkRepo.getCheck(checkId).split(
                ifSuccess = { check ->
                    onCheckRetrieved(check)
                },
                ifError = {
                    onRetrieveCheckError(it)
                }
            )
        }
    }

    private fun onRetrieveCheckError(it: ApiResponse.Error) {
        _uiState.value = OpenCheckUiState.ErrorState(
            ErrorHelper.getErrorMessage(
                arrayOfNonNull(it.firstErrorCode()),
                stringProvider
            )
        )
    }

    private suspend fun onCheckRetrieved(check: Check) {
        this.check = check
        determineCheckState(check)
        if (houseCreditManager.availableHouseCredit == null) {
            fetchHouseCredit()
        }
        buildAndEmitCheckItems()
    }

    private fun determineCheckState(check: Check) {
        this.checkState = when (checkState) {
            CheckState.Undetermined -> {
                when {
                    check.isClosedOrPaid -> {
                        CheckState.Paid
                    }
                    check.discounts.isNullOrEmpty().not() -> {
                        CheckState.Paying
                    }
                    else -> {
                        CheckState.Working
                    }
                }
            }
            else -> this.checkState
        }
    }

    private suspend fun fetchHouseCredit() {
        houseCreditManager.fetchHouseCredit().split(
            ifSuccess = {
                this.fetchHouseCreditError = null
            },
            ifError = {
                this.fetchHouseCreditError = it
            }
        )
    }

    private fun buildAndEmitCheckItems() {
        this.check ?: return

        if (this.check!!.isEmpty) {
            _uiState.value = OpenCheckUiState.Empty(System.currentTimeMillis())
            return
        }

        when (this.checkState) {
            CheckState.Working -> {
                emitWorkingCheckItems()
            }
            CheckState.Paying -> {
                emitPayingCheckItems()
            }
            CheckState.Paid -> {
                emitPaidCheckItems()
            }
            CheckState.Undetermined -> {
                ErrorReporter.logException(Throwable("Check $checkId state undetermined"))
                showGenericErrorState()
            }
        }
    }

    private fun showGenericErrorState() {
        _uiState.value = OpenCheckUiState.ErrorState(
            ErrorHelper.getErrorMessage(
                errorCodes = emptyArray(),
                stringProvider = stringProvider
            )
        )
    }

    private fun emitPaidCheckItems() {
        _uiState.value = OpenCheckUiState.CheckPaid
    }

    private fun emitPayingCheckItems() {
        buildPayingCheckItems().let { items ->
            _uiState.value = OpenCheckUiState.Paying(
                updatedAt = System.currentTimeMillis(),
                items = items,
                paymentMethod = paymentMethodManager.selectedPaymentMethod,
                paymentInProgress = false
            )
        }
    }

    private fun emitWorkingCheckItems() {
        buildWorkingCheckItems().let { items ->
            _uiState.value = OpenCheckUiState.Working(
                System.currentTimeMillis(),
                items
            )
        }
    }

    private fun buildWorkingCheckItems(): List<CheckItem> {
        return buildWorkingCheckItems(check, this)
    }

    private fun buildPayingCheckItems(): List<CheckItem> {
        return buildPayingCheckItems(
            check = check,
            openCheckCallbacks = this,
            applyDiscountError = applyDiscountError,
            fetchHouseCreditError = fetchHouseCreditError,
            payCheckError = payCheckError
        )
    }

    fun refreshCheck(fromUserInteraction: Boolean = false) {
        if (fromUserInteraction) {
            viewModelScope.launch(coroutineExceptionHandler) {
                paymentMethodManager.fetchPaymentMethodIfNeeded()
                houseCreditManager.fetchHouseCreditIfNeeded()
                retrieveCheck()
            }
        } else {
            retrieveCheck()
        }
    }

    fun acceptHousePayTerms() {
        viewModelScope.launch(ioDispatcher + coroutineExceptionHandler) {
            _uiState.value = OpenCheckUiState.Loading
            zipRequestsUtil.issueApiCall(
                PatchAccountAttributesRequest(
                    AccountUpdate(
                        analyticsConsent = null,
                        termsConditionsConsent = null,
                        housePayTermsConditionsConsent = true
                    )
                )
            ).fold(
                ifError = {
                    _uiState.value = OpenCheckUiState.ErrorState(
                        ErrorHelper.getErrorMessage(
                            errorCodes = (it as? ServerError.ApiError?)?.errorCodes ?: emptyArray(),
                            stringProvider = stringProvider
                        )
                    )
                },
                ifValue = {
                    userManager.didConsentHousePayTermsConditions = true
                },
                ifEmpty = {}
            )
            emitPayingCheckItems()
        }
    }

    fun onSettleCheckClick() {
        if (discountManager.shouldApplyDiscount) {
            emitEvent(
                OpenCheckEventBuilder.buildDiscountWarningEvent(
                    stringProvider,
                    checkCallbacks = this@OpenCheckViewModel,
                    showU27warning = discountManager.shouldShowU27DiscountBanner
                )
            )
        } else {
            goToPaying()
        }
    }

    private fun goToPaying() {
        checkState = CheckState.Paying
        buildAndEmitCheckItems()
    }

    private fun doApplyDiscount() {
        val check = this.check ?: return

        _uiState.value = OpenCheckUiState.Loading
        viewModelScope.launch(coroutineExceptionHandler) {
            checkRepo.postCheckDiscount(check.id).split(
                ifSuccess = {
                    this@OpenCheckViewModel.check = it
                    this@OpenCheckViewModel.applyDiscountError = null
                    goToPaying()
                },
                ifError = {
                    onApplyDiscountError(it)
                }
            )
        }
    }

    private fun onApplyDiscountError(error: ApiResponse.Error) {
        this.applyDiscountError = error
        goToPaying()
    }

    fun onCustomTipConfirmed(tipAmountCents: Int) {
        tipManager.tip = Tip.CustomAmount(tipAmountCents)
        buildAndEmitCheckItems()
    }

    fun onHouseCreditConfirmed(amountCents: Int) {
        houseCreditManager.usingHouseCreditCents = amountCents
        buildAndEmitCheckItems()
    }

    fun onPaymentMethodClick() {
        emitEvent(OpenCheckEvent.OpenPaymentMethod)
    }

    fun onPaymentMethodConfirm() {
        buildAndEmitCheckItems()
    }

    fun onPayNowClick() {
        if (userManager.didConsentHousePayTermsConditions) {
            payCheck()
        } else {
            emitEvent(OpenCheckEvent.ShowHousePayTerms)
        }
    }

    private fun payCheck() {
        tipManager.removeTipIfAlreadyPaid()
        _uiState.editAndEmit<OpenCheckUiState.Paying> { it.copy(paymentInProgress = true) }
        viewModelScope.launch(coroutineExceptionHandler) {
            when (val result = payCheck(getPayCheckParams())) {
                is PayCheckResult.PayCheckFailure -> {
                    _uiState.editAndEmit<OpenCheckUiState.Paying> { it.copy(paymentInProgress = false) }
                    handlePayCheckFailure(result)
                }
                is PayCheckResult.PayCheckSuccess -> {
                    goToPaid()
                }
            }
        }
    }

    private fun goToPaid() {
        checkState = CheckState.Paid
        buildAndEmitCheckItems()
    }

    private fun handlePayCheckFailure(failure: PayCheckResult.PayCheckFailure) {
        when (failure.error) {
            is CardError, is CardSyncFailed, is CheckOutOfDate, is CreditFail, NoPaymentMethod,
            is CreditPaidThenPayFailed, is InsufficientCredit, NoAmount, NoCheck, is Unknown -> {
                emitErrorDialogEvent(failure.error.error) {}
            }
            CheckClosed -> {
                emitErrorDialogEvent(failure.error.error) {
                    emitEvent(OpenCheckEvent.GoToReceipt(checkId))
                }
            }
            is PostDownloadFailed -> {
                emitErrorDialogEvent(failure.error.error) {
                    emitEvent(OpenCheckEvent.DismissSelf)
                }
            }
            is CheckOpenOnWorkStation -> {
                payCheckError = failure.error.error
                buildAndEmitCheckItems()
            }
        }
    }

    private fun emitErrorDialogEvent(
        error: ApiResponse.Error?,
        onDismiss: () -> Unit
    ) {
        emitEvent(
            OpenCheckEventBuilder.buildShowErrorDialogEvent(error, stringProvider, onDismiss)
        )
    }

    private fun getPayCheckParams() = PayCheckParams(
        check = check,
        payCheckPaymentInfo = paymentMethodManager.selectedPaymentMethod?.toPayCheckPaymentInfo(),
        amountCents = maxOf(
            0,
            (check?.remainingCents ?: 0) - houseCreditManager.usingHouseCreditCents
        ),
        creditCents = houseCreditManager.usingHouseCreditCents,
        tipCents = tipManager.tipValueCents
    )

    override val onRetryPayCheck: () -> Unit = {
        onPayNowClick()
    }

    override val onHouseCreditTsAndCsClick: () -> Unit = {
        viewModelScope.launch {
            emitEvent(
                OpenCheckEvent.OpenUrlInWebView(
                    HousePayConstants.HOUSE_CREDIT_TS_AND_CS_URL,
                    requiresAuth = true
                )
            )
        }
    }

    override val onUseHouseCreditClick: () -> Unit = {
        emitEvent(
            OpenCheckEventBuilder.buildOpenHouseCreditInputEvent(
                houseCreditManager,
                tipManager,
                check
            )
        )
    }

    override val onRetryFetchHouseCredit: () -> Unit = {
        _uiState.value = OpenCheckUiState.Loading
        viewModelScope.launch(coroutineExceptionHandler) {
            fetchHouseCredit()
            buildAndEmitCheckItems()
        }
    }

    override val onU27AlertBannerClick: () -> Unit = {
        emitEvent(OpenCheckEvent.ShowU27DiscountDetailsModal)
    }

    override val onRetryApplyDiscount: () -> Unit = {
        doApplyDiscount()
    }

    override val onTipOptionClick: (tipOption: TipOptionUiModel) -> Unit = {
        when (it) {
            is TipOptionUiModel.CustomTipOptionUiModel -> {
                emitEvent(
                    OpenCheckEventBuilder.buildOpenCustomTipInputEvent(check, tipManager)
                )
            }
            is TipOptionUiModel.PercentageTipOptionUiModel -> {
                tipManager.tip = if (it.isSelected) {   //already selected, remove tip
                    Tip.NoTip
                } else {
                    Tip.Percentage(it.percentage)
                }
                buildAndEmitCheckItems()
            }
        }
    }

    private fun emitEvent(event: OpenCheckEvent) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _event.emit(event)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(checkId: String): OpenCheckViewModel
    }
}
