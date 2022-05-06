package com.sohohouse.seven.book.eventdetails.payment

import android.annotation.SuppressLint
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BasePresenter
import com.sohohouse.seven.base.error.ErrorDialogPresenter
import com.sohohouse.seven.base.error.ErrorViewStatePresenter
import com.sohohouse.seven.base.load.PresenterLoadable
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.models.Card
import com.sohohouse.seven.network.core.request.GetPaymentRequest
import com.sohohouse.seven.payment.AddPaymentItem
import com.sohohouse.seven.payment.BasePaymentItem
import com.sohohouse.seven.payment.TextPaymentItem
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber

abstract class BasePaymentPresenter<V : BasePaymentController>(
    private val zipRequestsUtil: ZipRequestsUtil,
    private val userManager: UserManager
) : BasePresenter<V>(), PresenterLoadable<V>, ErrorDialogPresenter<V>, ErrorViewStatePresenter<V> {

    abstract fun onDataFetched(value: List<Card>)

    @SuppressLint("CheckResult")
    open fun fetchData() {
        zipRequestsUtil.issueApiCall(GetPaymentRequest(true))
            .observeOn(AndroidSchedulers.mainThread())
            .compose(loadTransformer())
            .compose(errorViewStateTransformer())
            .subscribe { response ->
                when (response) {
                    is Either.Error -> {
                        Timber.d(response.error.toString())
                    }
                    is Either.Value -> {
                        onDataFetched(response.value)
                    }
                }
            }
    }

    fun setupPaymentMethodItems(
        cardItems: List<BasePaymentItem>,
        callback: (items: List<BasePaymentItem>) -> Unit
    ) {
        val dataItems = mutableListOf<BasePaymentItem>()
        val supportingMsg =
            if (userManager.subscriptionType == SubscriptionType.FRIENDS) R.string.payment_methods_supporting_friends else R.string.payment_methods_supporting
        dataItems.add(TextPaymentItem(supportingMsg))
        dataItems.addAll(cardItems)
        dataItems.add(AddPaymentItem)

        callback(dataItems)
    }

    override fun reloadDataAfterError() {
        fetchData()
    }

    fun refreshData() {
        fetchData()
    }

    fun getNoPaymentMethodsMessage(): Int {
        return when (userManager.subscriptionType) {
            SubscriptionType.FRIENDS -> R.string.payment_methods_empty_supporting
            else -> R.string.payment_methods_empty_supporting_friends
        }
    }
}