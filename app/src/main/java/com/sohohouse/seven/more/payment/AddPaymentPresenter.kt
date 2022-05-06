package com.sohohouse.seven.more.payment

import android.annotation.SuppressLint
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BasePresenter
import com.sohohouse.seven.base.error.ErrorDialogPresenter
import com.sohohouse.seven.base.error.ErrorViewStatePresenter
import com.sohohouse.seven.base.load.PresenterLoadable
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.analytics.AnalyticsEvent
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.common.utils.encryption.PublicKeyEncryptable
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.error
import com.sohohouse.seven.network.core.request.GetPaymentFormRequest
import com.sohohouse.seven.network.core.request.PostPaymentCardRequest
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AddPaymentPresenter @Inject constructor(
    private val zipRequestsUtil: ZipRequestsUtil,
    private val publicKeyEncryptor: PublicKeyEncryptable,
    private val analyticsManager: AnalyticsManager,
    private val userManager: UserManager
) : BasePresenter<AddPaymentViewController>(), PresenterLoadable<AddPaymentViewController>,
    ErrorDialogPresenter<AddPaymentViewController>,
    ErrorViewStatePresenter<AddPaymentViewController> {
    companion object {
        private const val TAG = "AddPaymentPresenter"
    }

    private lateinit var publicPGPKey: String

    override fun onAttach(
        view: AddPaymentViewController,
        isFirstAttach: Boolean,
        isRecreated: Boolean
    ) {
        super.onAttach(view, isFirstAttach, isRecreated)
        if (isFirstAttach) {
            fetchPaymentForm()
        }
    }

    @SuppressLint("CheckResult")
    private fun fetchPaymentForm() {
        zipRequestsUtil.issueApiCall(GetPaymentFormRequest())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(loadTransformer())
            .compose(errorViewStateTransformer())
            .subscribe(Consumer {
                when (it) {
                    is Either.Error -> {
                    }
                    is Either.Value -> {
                        val form = it.value
                        form.paymentFormFields?.let { fields ->
                            publicPGPKey = form.publicKey
                            executeWhenAvailable { view, _, _ ->
                                view.initLayout(form.id, fields)
                            }
                        }
                    }
                }
            })
    }

    @SuppressLint("CheckResult")
    fun onAddButtonClicked(id: String, values: List<Pair<String, String>>) {
        val plainTextPayload = generatePlaintextPayload(values)
        publicKeyEncryptor.encrypt(plainTextPayload, publicPGPKey)
            .subscribeOn(Schedulers.computation())
            .flatMap { encryptedPayload ->
                if (encryptedPayload.isEmpty()) {
                    Single.just(error(ServerError.BAD_REQUEST))
                } else {
                    zipRequestsUtil.issueApiCall(PostPaymentCardRequest(encryptedPayload, id))
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .compose(loadTransformer())
            .compose(errorDialogTransformer())
            .subscribe(Consumer {
                when (it) {
                    is Either.Error -> {
                        analyticsManager.track(AnalyticsEvent.PaymentMethods.Add.Failure(it.error.toString()))
                    }
                    is Either.Value -> {
                        analyticsManager.track(AnalyticsEvent.PaymentMethods.Add.Success)
                        executeWhenAvailable { view, _, _ ->
                            val card = it.value
                            view.addCardSuccess(card)
                        }
                    }
                }
            })
    }

    fun flipExpiryDate(value: String): String {
        val mid = value.length / 2 + value.length % 2
        return value.substring(mid).plus(value.substring(0, mid))
    }

    private fun generatePlaintextPayload(values: List<Pair<String, String>>): String {
        return values
            .map { "${it.first}=${it.second}" }
            .reduce { acc, value -> "$acc,$value" }
    }

    //region Error
    override fun reloadDataAfterError() {
        fetchPaymentForm()
    }

    fun getPaymentMethodsNewSupportingMsg(): Int {
        return when (userManager.subscriptionType) {
            SubscriptionType.FRIENDS -> R.string.payment_methods_new_supporting_friends
            else -> R.string.payment_methods_new_supporting
        }
    }
    //endregion Error
}

