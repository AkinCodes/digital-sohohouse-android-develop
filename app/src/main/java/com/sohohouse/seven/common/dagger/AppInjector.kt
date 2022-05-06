package com.sohohouse.seven.common.dagger

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.sohohouse.seven.App
import com.sohohouse.seven.BuildConfigManager
import com.sohohouse.seven.common.dagger.component.AppComponent
import com.sohohouse.seven.common.dagger.component.DaggerAppComponent
import dagger.android.AndroidInjection
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection

object AppInjector {

    @JvmStatic
    fun init(application: App, buildConfigManager: BuildConfigManager): AppComponent {
        val component = DaggerAppComponent.builder()
            .application(application)
            .context(application)
            .authHostName(buildConfigManager.authHostName)
            .coreServiceHostName(buildConfigManager.coreHostName)
            .forceUpdateHostName(buildConfigManager.forceUpdateHostName)
            .sendBirdBaseUrl(buildConfigManager.sendBirdBaseUrl)
            .build()
            .apply { inject(application) }

        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                handleActivity(activity, component)
            }

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        })

        return component
    }

    private fun handleActivity(activity: Activity, component: AppComponent) {
        if (activity is HasAndroidInjector || activity is Injectable) {
            AndroidInjection.inject(activity)
        }
        if (activity is FragmentActivity) {
            activity.supportFragmentManager
                .registerFragmentLifecycleCallbacks(
                    object : FragmentManager.FragmentLifecycleCallbacks() {
                        override fun onFragmentPreCreated(
                            fm: FragmentManager,
                            f: Fragment,
                            savedInstanceState: Bundle?
                        ) {
                            if (f is Injectable) {
                                AndroidSupportInjection.inject(f)
                            }
                        }

                        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                            super.onFragmentStarted(fm, f)
                            component.analyticsManager.setScreenName(
                                activity.localClassName,
                                f::class.java.simpleName
                            )
                        }
                    }, true
                )
        }
    }
}