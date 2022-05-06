package com.sohohouse.seven.app

import com.sohohouse.seven.App

class TestApp : App() {

    override fun shouldRegisterFcmToken(): Boolean {
        return false
    }

    override fun shouldCreateAppComponent(): Boolean {
        return false
    }

    override fun shouldInitWorkManager(): Boolean {
        return false
    }

}
