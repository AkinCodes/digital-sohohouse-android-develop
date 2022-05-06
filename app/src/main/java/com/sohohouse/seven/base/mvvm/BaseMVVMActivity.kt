package com.sohohouse.seven.base.mvvm

import android.content.Context
import androidx.lifecycle.*
import com.sohohouse.seven.base.InjectableActivity
import com.sohohouse.seven.common.extensions.observeOnce
import com.sohohouse.seven.common.views.inappnotification.InAppNotification
import com.sohohouse.seven.common.views.inappnotification.InAppNotificationImpl
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseMVVMActivity<VM : BaseViewModel> : InjectableActivity(), ViewController,
    InAppNotification by InAppNotificationImpl() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val _context: Context?
        get() = this

    override val lifecycleOwner: LifecycleOwner
        get() = this

    override val viewModel by lazy { ViewModelProvider(this, viewModelFactory).get(viewModelClass) }

    abstract val viewModelClass: Class<VM>

    init {
        lifecycleScope.launch {
            whenCreated { observeAnalyticsEvents() }
            whenResumed { viewModel.onScreenViewed() }
        }
    }

    private fun observeAnalyticsEvents() {
        viewModel.screenNameEvent.observeOnce(this, Observer { screen ->
            screen?.let { viewModel.setScreenName(name= it) }
        })
    }

    override fun onConnected() {
        viewModel.logConnected()
    }

    override fun onDisconnected() {
        viewModel.logDisconnected()
    }

}