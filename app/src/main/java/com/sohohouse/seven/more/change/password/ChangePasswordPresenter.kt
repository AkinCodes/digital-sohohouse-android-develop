package com.sohohouse.seven.more.change.password

import android.annotation.SuppressLint
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.base.BasePresenter
import com.sohohouse.seven.base.error.ErrorDialogPresenter
import com.sohohouse.seven.base.load.PresenterLoadable
import com.sohohouse.seven.common.analytics.AnalyticsEvent
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.network.base.model.Either
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class ChangePasswordPresenter @Inject constructor(
    private val accountInteractor: AccountInteractor,
    private val analyticsManager: AnalyticsManager,
    private val firebaseEventTracking: AnalyticsManager
) :
    BasePresenter<ChangePasswordViewController>(),
    PresenterLoadable<ChangePasswordViewController>,
    ErrorDialogPresenter<ChangePasswordViewController> {

    override fun onAttach(
        view: ChangePasswordViewController,
        isFirstAttach: Boolean,
        isRecreated: Boolean
    ) {
        super.onAttach(view, isFirstAttach, isRecreated)
        view.setScreenName(AnalyticsManager.Screens.ChangePassword.name)
    }

    @SuppressLint("CheckResult")
    fun onChangePasswordRequested(
        oldPassword: String,
        newPassword: String,
        newPasswordConfirm: String,
        clientSecret: String,
        clientID: String
    ) {
        accountInteractor.getCompleteAccount()
            .flatMap { either ->
                return@flatMap when (either) {
                    is Either.Value -> {
                        val email = either.value.email
                        accountInteractor.changePassword(
                            email,
                            oldPassword,
                            newPassword,
                            newPasswordConfirm,
                            clientSecret,
                            clientID
                        )
                    }
                    is Either.Error -> Single.just(either)
                    is Either.Empty -> Single.just(either)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .compose(loadTransformer())
            .compose(errorDialogTransformer())
            .subscribeOn(Schedulers.io())
            .subscribe({
                when (it) {
                    is Either.Error -> {
                        Timber.d(it.error.toString())
                        analyticsManager.track(AnalyticsEvent.ChangePassword.Failure(it.error.toString()))
                    }
                    is Either.Value -> {
                        analyticsManager.track(AnalyticsEvent.ChangePassword.Success)
                        executeWhenAvailable { view, _, _ ->
                            view.onPasswordChanged()
                        }
                    }
                }
            }, { throwable ->
                FirebaseCrashlytics.getInstance().recordException(throwable)
            })
    }

    fun logChangePasswordView() {
        firebaseEventTracking.logEventAction(AnalyticsManager.Action.AccountChangePassword)
    }
}