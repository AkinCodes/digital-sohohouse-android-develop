package com.sohohouse.seven.branding

import com.sohohouse.seven.R
import com.sohohouse.seven.base.InjectableActivity

class AppIconActivity : InjectableActivity() {

    override fun getContentLayout(): Int = R.layout.activity_app_icon

    override fun setBrandingTheme() {
        setTheme(themeManager.darkTheme)
    }
}