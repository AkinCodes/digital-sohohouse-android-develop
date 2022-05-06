package com.sohohouse.seven.authentication

import android.os.Bundle
import android.view.WindowManager
import com.sohohouse.seven.BuildConfig
import com.sohohouse.seven.R
import com.sohohouse.seven.base.InjectableActivity
import com.uxcam.UXCam

class AuthenticationActivity : InjectableActivity() {

    companion object {
        const val LOGIN_SCREEN = "Login Screen"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!BuildConfig.DEBUG) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        UXCam.tagScreenName(LOGIN_SCREEN)
    }

    override fun setBrandingTheme() {
        setTheme(R.style.BaseTheme)
    }

    override fun getContentLayout(): Int = R.layout.activity_authentication
}
