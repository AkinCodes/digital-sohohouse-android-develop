package com.sohohouse.seven.common.extensions

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.sohohouse.seven.common.views.TextWatcherAdapter


fun EditText.focusAndShowKeyboard() {
    /**
     * This is to be called when the window already has focus.
     * Ripped from <a href="https://developer.squareup.com/blog/showing-the-android-keyboard-reliably/">Square's blog post</a>
     */
    fun View.showTheKeyboardNow() {
        if (isFocused) {
            post {
                // We still post the call, just in case we are being notified of the windows focus
                // but InputMethodManager didn't get properly setup yet.
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    requestFocus()
    if (hasWindowFocus()) {
        // No need to wait for the window to get focus.
        showTheKeyboardNow()
    } else {
        // We need to wait until the window gets focus.
        viewTreeObserver.addOnWindowFocusChangeListener(
            object : ViewTreeObserver.OnWindowFocusChangeListener {
                override fun onWindowFocusChanged(hasFocus: Boolean) {
                    // This notification will arrive just before the InputMethodManager gets set up.
                    if (hasFocus) {
                        this@focusAndShowKeyboard.showTheKeyboardNow()
                        // It’s very important to remove this listener once we are done.
                        viewTreeObserver.removeOnWindowFocusChangeListener(this)
                    }
                }
            })
    }
}

inline fun EditText.onKey(
    key: Int,
    consumeEvent: Boolean = true,
    crossinline function: () -> Unit
) {
    setOnEditorActionListener { _, _, event ->
        if (event?.keyCode == key) {
            function()
            return@setOnEditorActionListener consumeEvent
        }
        false
    }
}

inline fun EditText.onImeAction(
    imeActionId: Int,
    consumeEvent: Boolean = true,
    crossinline function: () -> Unit
) {
    setOnEditorActionListener { _, actionId, event ->
        if (actionId == imeActionId) {
            function()
            return@setOnEditorActionListener consumeEvent
        }
        false
    }
}

inline fun EditText.onTextChanged(crossinline function: (text: CharSequence?, count: Int) -> Unit) {
    addTextChangedListener(object : TextWatcherAdapter() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            function(s, count)
        }
    })
}

inline fun EditText.afterTextChanged(crossinline function: (text: Editable?) -> Unit) {
    addTextChangedListener(object : TextWatcherAdapter() {
        override fun afterTextChanged(s: Editable?) {
            function(s)
        }
    })
}

fun EditText.setCharacterLimit(limit: Int) {
    filters = arrayOf<InputFilter>(InputFilter.LengthFilter(limit))
}