package com.sohohouse.seven.common.securewebview

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.sohohouse.seven.base.mvpimplementation.ActivityLifeCycleListener

class SecureWebViewLifeCycleListener(private val activity: Activity?) : ActivityLifeCycleListener {
    override fun onResume() {
        //do nothing
    }

    override fun onPostCreated(activity: Activity?, savedInstanceState: Bundle?) {
        //do nothing
    }

    override fun onPause() {
        //do nothing
    }

    override fun onDestroy() {
        if (activity != null
            && activity is SecureWebViewListener
        ) {
            activity.destroyWebView()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //do nothing
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        //do nothing
    }
}