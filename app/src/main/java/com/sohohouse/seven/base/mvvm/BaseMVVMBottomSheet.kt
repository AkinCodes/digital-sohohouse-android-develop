package com.sohohouse.seven.base.mvvm

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sohohouse.seven.base.BaseBottomSheet
import com.sohohouse.seven.common.dagger.Injectable
import javax.inject.Inject

abstract class BaseMVVMBottomSheet<VM : BaseViewModel> : BaseBottomSheet(), ViewController,
    Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val viewModel by lazy { ViewModelProvider(this, viewModelFactory).get(viewModelClass) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAnalyticsEvents()
    }

    private fun observeAnalyticsEvents() {
        viewModel.screenNameEvent.observe(viewLifecycleOwner, Observer { screen ->
            screen?.let { viewModel.setScreenName(name= screen) }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.onScreenViewed()
    }

    abstract val viewModelClass: Class<VM>

    override val _context: Context?
        get() = context

    override val lifecycleOwner: LifecycleOwner
        get() = this

}