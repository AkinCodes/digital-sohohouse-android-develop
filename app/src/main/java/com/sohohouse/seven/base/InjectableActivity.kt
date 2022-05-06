package com.sohohouse.seven.base

import com.sohohouse.seven.branding.ThemeManager
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

abstract class InjectableActivity : BaseActivity(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

    @Inject
    lateinit var themeManager: ThemeManager

    override fun setBrandingTheme() {
        setTheme(themeManager.lightTheme)
    }
}