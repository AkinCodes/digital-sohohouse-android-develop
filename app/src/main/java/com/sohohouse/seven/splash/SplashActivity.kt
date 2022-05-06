package com.sohohouse.seven.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.sohohouse.seven.R
import com.sohohouse.seven.branding.ThemeManager
import com.sohohouse.seven.common.dagger.Injectable
import com.sohohouse.seven.common.extensions.startActivityAndFinish
import com.sohohouse.seven.databinding.ActivitySplashBinding
import com.sohohouse.seven.network.utils.NetworkUtils
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class SplashActivity : AppCompatActivity(), Injectable, HasAndroidInjector {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var themeManager: ThemeManager

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

    private lateinit var binding: ActivitySplashBinding

    private val viewModel: SplashViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[SplashViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(themeManager.darkTheme)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        setupViewModels()
        viewModel.uxCamVendor.setUp()
    }

    private fun setupViews() {
        binding.errorState.getReloadButton().setOnClickListener {
            if (NetworkUtils.isNetworkConnected(this)) {
                dismissError()
                viewModel.loadRequest(this, intent)
            }
        }
        dismissError()
    }

    private fun setupViewModels() {
        viewModel.navigation.observe(this) { intent ->
            startActivityAndFinish(intent)
        }
        viewModel.networkError.observe(this) {
            showError()
        }

        viewModel.loadRequest(this, intent)
    }

    private fun showError() {
        binding.viewSwitcher.apply {
            if (nextView.id == R.id.error_state) showNext()
        }
    }

    private fun dismissError() {
        binding.viewSwitcher.apply {
            if (nextView.id != R.id.error_state) showNext()
        }
    }

}