package com.sohohouse.seven.common.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import java.io.Serializable


fun Activity.getSerializable(key: String, savedInstanceState: Bundle?): Serializable? {
    return savedInstanceState?.getSerializable(key)
        ?: intent.getSerializableExtra(key)
}

fun Activity.hideKeyboard() {
    val view = findViewById<View>(android.R.id.content)
    if (view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun Activity.startActivityAndFinish(intent: Intent) {
    startActivity(intent)
    finish()
}

fun Window.getWindowInsetsController(): WindowInsetsControllerCompat {
    return WindowInsetsControllerCompat(this, this.decorView)
}

fun Activity.enterToImmersiveMode(
    onWindowFocusChangeListener: ViewTreeObserver.OnWindowFocusChangeListener? = null,
) {
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    window.getWindowInsetsController().apply {
        hide(WindowInsetsCompat.Type.systemBars())
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    onWindowFocusChangeListener?.let {
        window.decorView.viewTreeObserver?.addOnWindowFocusChangeListener(it)
    }
}

fun Activity.exitImmersiveModeIfNeeded(
    onWindowFocusChangeListener: ViewTreeObserver.OnWindowFocusChangeListener? = null,
) {
    if (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON and window.attributes.flags == 0) {
        // We left immersive mode already.
        return
    }
    onWindowFocusChangeListener?.let {
        window.decorView.viewTreeObserver?.removeOnWindowFocusChangeListener(it)
    }
    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    window.getWindowInsetsController().apply {
        show(WindowInsetsCompat.Type.systemBars())
    }
}