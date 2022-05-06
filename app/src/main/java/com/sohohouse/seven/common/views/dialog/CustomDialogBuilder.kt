package com.sohohouse.seven.common.views.dialog

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.extensions.withResultListener

abstract class CustomDialogBuilder<T : DialogFragment> {

    companion object {
        const val REQ_KEY_POSITIVE_BTN_CLICK = "REQ_KEY_POSITIVE_BTN_CLICK"
        const val REQ_KEY_NEGATIVE_BTN_CLICK = "REQ_KEY_NEGATIVE_BTN_CLICK"
    }

    private var positiveBtnText: String? = null
    private var negativeBtnText: String? = null
    private var positiveBtnClickListener: (() -> Unit)? = null
    private var negativeBtnClickListener: (() -> Unit)? = null
    private var title: String? = null
    private var message: String? = null

    fun withPositiveBtnText(positiveBtnText: String?) =
        apply { this.positiveBtnText = positiveBtnText }

    fun withNegativeBtnText(negativeBtnText: String?) =
        apply { this.negativeBtnText = negativeBtnText }

    fun withPositiveBtnClickListener(positiveBtnClickListener: () -> Unit) =
        apply { this.positiveBtnClickListener = positiveBtnClickListener }

    fun withNegativeBtnClickListener(negativeBtnClickListener: () -> Unit) =
        apply { this.negativeBtnClickListener = negativeBtnClickListener }

    fun withTitle(title: String?) = apply { this.title = title }
    fun withMessage(message: String?) = apply { this.message = message }

    protected fun propertiesAsBundle(): Bundle {
        return Bundle().apply {
            putString(BundleKeys.TITLE, this@CustomDialogBuilder.title)
            putString(BundleKeys.MESSAGE, this@CustomDialogBuilder.message)
            putString(BundleKeys.POSITIVE_BTN_TEXT, this@CustomDialogBuilder.positiveBtnText)
            putString(BundleKeys.NEGATIVE_BTN_TEXT, this@CustomDialogBuilder.negativeBtnText)
        }
    }

    abstract fun newInstance(): T

    open fun build(): T {
        return newInstance()
            .withResultListener(REQ_KEY_POSITIVE_BTN_CLICK) { _, _ ->
                positiveBtnClickListener?.invoke()
            }
            .withResultListener(REQ_KEY_NEGATIVE_BTN_CLICK) { _, _ ->
                negativeBtnClickListener?.invoke()
            }
            .apply {
                arguments = propertiesAsBundle()
            }
    }
}