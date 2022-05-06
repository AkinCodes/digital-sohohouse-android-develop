package com.sohohouse.seven.more.payment

import android.annotation.SuppressLint
import com.sohohouse.seven.common.analytics.AnalyticsEvent
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.book.eventdetails.payment.BasePaymentPresenter
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.models.Card
import com.sohohouse.seven.network.core.request.DeletePaymentRequest
import com.sohohouse.seven.network.core.request.PatchDefaultPaymentRequest
import com.sohohouse.seven.payment.CardPaymentItem
import com.sohohouse.seven.payment.PaymentCardStatus
import com.sohohouse.seven.payment.PaymentCardType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import javax.inject.Inject

class MorePaymentPresenter @Inject constructor(
    private val zipRequestsUtil: ZipRequestsUtil,
    private val analyticsManager: AnalyticsManager,
    userManager: UserManager
) :
    BasePaymentPresenter<MorePaymentViewController>(zipRequestsUtil, userManager) {

    companion object {
        private const val TAG = "MorePaymentPresenter"
    }

    override fun onAttach(
        view: MorePaymentViewController,
        isFirstAttach: Boolean,
        isRecreated: Boolean
    ) {
        super.onAttach(view, isFirstAttach, isRecreated)
        view.setScreenName(AnalyticsManager.Screens.PaymentMethods.name)
        if (isFirstAttach) {
            fetchData()
        }
    }

    override fun onDataFetched(value: List<Card>) {
        if (value.isEmpty()) {
            executeWhenAvailable { view, _, _ -> view.showEmptyView() }
        } else {
            executeWhenAvailable { view, _, _ ->
                setupPaymentMethodItems(value.map {
                    CardPaymentItem(
                        it.id,
                        PaymentCardType.valueOf(it.cardType),
                        it.lastFour,
                        it.isPrimary,
                        PaymentCardStatus.valueOf(it.status)
                    )
                }, view::onDataReady)
            }
        }
    }

    @SuppressLint("CheckResult")
    fun deletePaymentMethod(id: String) {
        zipRequestsUtil.issueApiCall(DeletePaymentRequest(id))
            .observeOn(AndroidSchedulers.mainThread())
            .compose(loadTransformer())
            .compose(errorDialogTransformer())
            .subscribe(Consumer {
                when (it) {
                    is Either.Error -> {
                        analyticsManager.track(AnalyticsEvent.PaymentMethods.Delete.Failure(it.error.toString()))
                        executeWhenAvailable { view, _, _ ->
                            view.showFailureView()
                        }
                    }
                    is Either.Empty -> {
                        analyticsManager.track(AnalyticsEvent.PaymentMethods.Delete.Success)
                        executeWhenAvailable { view, _, _ ->
                            view.paymentDeleted(id)
                        }
                    }
                }
            })
    }

    @SuppressLint("CheckResult")
    fun setDefaultPaymentMethod(id: String) {
        zipRequestsUtil.issueApiCall(PatchDefaultPaymentRequest(id))
            .observeOn(AndroidSchedulers.mainThread())
            .compose(loadTransformer())
            .compose(errorDialogTransformer())
            .subscribe(Consumer {
                when (it) {
                    is Either.Error -> {
                        analyticsManager.track(AnalyticsEvent.PaymentMethods.MakeDefault.Failure(it.error.toString()))
                        executeWhenAvailable { view, _, _ ->
                            view.showFailureView()
                        }
                    }
                    is Either.Value -> {
                        analyticsManager.track(AnalyticsEvent.PaymentMethods.MakeDefault.Success)
                        executeWhenAvailable { view, _, _ ->
                            view.defaultPaymentUpdated(it.value)
                        }
                    }
                }
            })
    }

    fun logAddPaymentClick() {
        analyticsManager.logEventAction(AnalyticsManager.Action.AddPaymentMethod)
    }

}

