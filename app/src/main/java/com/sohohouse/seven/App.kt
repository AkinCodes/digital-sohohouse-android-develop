package com.sohohouse.seven

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Configuration
import androidx.work.WorkManager
import com.salesforce.marketingcloud.InitializationStatus
import com.salesforce.marketingcloud.MarketingCloudConfig
import com.salesforce.marketingcloud.MarketingCloudSdk
import com.salesforce.marketingcloud.notifications.NotificationCustomizationOptions
import com.sendbird.android.SendBird
import com.sendbird.android.SendBirdException
import com.sendbird.android.handlers.InitResultHandler
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.dagger.AppInjector.init
import com.sohohouse.seven.common.dagger.component.AppComponent
import com.sohohouse.seven.common.remoteconfig.RemoteConfigManager
import com.sohohouse.seven.common.utils.BuildVariantConfig
import com.sohohouse.seven.common.uxcam.UXCamVendor
import com.uxcam.UXCam
import com.uxcam.datamodel.UXConfig
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import timber.log.Timber.Forest.plant
import javax.inject.Inject

open class App : Application(), HasAndroidInjector, LifecycleObserver {
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var workManagerConfig: Configuration

    @Inject
    lateinit var remoteConfigManager: RemoteConfigManager

    @Inject
    lateinit var uxCamVendor: UXCamVendor

    override fun androidInjector() = androidInjector

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onEnterForeground() {
        appComponent.analyticsManager.logEventAction(AnalyticsManager.Action.AppForegrounded,
            null)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onEnterBackground() {
        appComponent.analyticsManager.logEventAction(AnalyticsManager.Action.AppBackgrounded,
            null)
    }

    @SuppressLint("Deprecation")
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) plant(Timber.DebugTree())
        buildConfigManager = BuildConfigManager(this)
        if (shouldCreateAppComponent()) {
            val initResultHandler: InitResultHandler = object : InitResultHandler {
                override fun onMigrationStarted() {}
                override fun onInitSucceed() {}
                override fun onInitFailed(e: SendBirdException) {}
            }
            SendBird.init(buildConfigManager.sendBirdAppKey, this, false, initResultHandler)
            createAppComponent()
            remoteConfigManager.init(object : RemoteConfigManager.EventListener {
                override fun onCompleteListener() {
                    onRemoteConfigCompleteListener()
                }
            })
        }
        if (shouldInitWorkManager()) {
            initWorkManager()
        }
        observeProcessLifecycle()
        setUpMarketingCloud()
        if (shouldRegisterFcmToken()) {
            registerFcmToken()
        }
    }

    private fun onRemoteConfigCompleteListener() {
        if (remoteConfigManager.remoteConfig.getBoolean(IS_UXCAM_ENABLED_KEY)) {
            uxCamVendor.checkOptInStatusAndStartSession()
        }
    }

    private fun initWorkManager() {
        WorkManager.initialize(applicationContext, workManagerConfig)
    }

    protected open fun shouldRegisterFcmToken(): Boolean {
        return true
    }

    protected open fun shouldCreateAppComponent(): Boolean {
        return true
    }

    protected open fun shouldInitWorkManager(): Boolean {
        return true
    }

    private fun observeProcessLifecycle() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    private fun setUpMarketingCloud() {
        MarketingCloudSdk.init(this, MarketingCloudConfig.builder()
            .setApplicationId(BuildVariantConfig.SALESFORCE_APP_ID)
            .setAccessToken(BuildVariantConfig.SALESFORCE_ACCESS_TOKEN)
            .setSenderId(BuildVariantConfig.SALESFORCE_SENDER_ID)
            .setMarketingCloudServerUrl(BuildVariantConfig.SALESFORCE_ENDPOINT_URL)
            .setMid(BuildVariantConfig.SALESFORCE_MID)
            .setNotificationCustomizationOptions(createCustomOptions())
            .build(this)) { status: InitializationStatus? -> }
    }

    private fun registerFcmToken() {
        if (appComponent.authInteractor.token.isNotEmpty()) {
            appComponent.firebaseRegistrationService.registerFcmToken()
        }
    }

    private fun createAppComponent() {
        appComponent = init(this, buildConfigManager!!)
    }

    private fun createCustomOptions(): NotificationCustomizationOptions {
        return NotificationCustomizationOptions.create(R.drawable.ic_notification_soho_house)
    }

    companion object {
        lateinit var buildConfigManager: BuildConfigManager

        @JvmStatic
        @get:Deprecated("")
        lateinit var appComponent: AppComponent
            protected set
        var IS_UXCAM_ENABLED_KEY = "UXCAM_ENABLED"
        var isUXCamEnabled = false
    }
}