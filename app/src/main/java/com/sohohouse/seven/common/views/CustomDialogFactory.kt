package com.sohohouse.seven.common.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.res.ResourcesCompat
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.createFontSpannable
import com.sohohouse.seven.common.extensions.onImeAction
import com.sohohouse.seven.common.extensions.focusAndShowKeyboard

object CustomDialogFactory {
    fun createThemedAlertDialog(
        context: Context,
        title: String? = null,
        message: String,
        positiveButtonText: String? = null,
        negativeButtonText: String? = null,
        positiveClickListener: DialogInterface.OnClickListener? = null,
        negativeClickListener: DialogInterface.OnClickListener? = null,
        onCancelListener: DialogInterface.OnCancelListener? = null
    ): AlertDialog {
        return createSimpleAlertDialog(
            context = context,
            title = title,
            message = message,
            positiveButtonText = positiveButtonText,
            negativeButtonText = negativeButtonText,
            titleTextSize = R.dimen.dp_24,
            messageTextSize = R.dimen.dp_16,
            buttonsTextSize = R.dimen.dp_14,
            normalFontResId = R.font.faro_lucky_regular,
            semiBoldFontResId = R.font.faro_lucky_regular,
            boldFontResId = R.font.faro_lucky_regular,
//                positiveColorResId = R.color.white,
//                negativeColorResId = R.color.white,
//                textColorResId = R.color.white,
            positiveClickListener = positiveClickListener,
            negativeClickListener = negativeClickListener,
            onCancelListener = onCancelListener,
            dialogTheme = R.style.Dialog
        )
    }

    fun createThemedInputDialog(
        context: Context,
        title: String? = null,
        message: String? = null,
        inputValue: String? = null,
        hint: String? = null,
        positiveButtonText: String? = null,
        negativeButtonText: String? = null,
        onInputConfirmed: (input: String) -> Unit,
        negativeClickListener: DialogInterface.OnClickListener? = null,
        onCancelListener: DialogInterface.OnCancelListener? = null
    ): AlertDialog {
        return createSimpleAlertDialog(
            context = context,
            title = title,
            message = message,
            positiveButtonText = positiveButtonText,
            negativeButtonText = negativeButtonText,
            titleTextSize = R.dimen.sp_20,
            messageTextSize = R.dimen.sp_16,
            buttonsTextSize = R.dimen.sp_14,
            normalFontResId = R.font.faro_lucky_regular,
            semiBoldFontResId = R.font.faro_lucky_regular,
            boldFontResId = R.font.faro_lucky_regular,
//                positiveColorResId = R.color.white,
//                negativeColorResId = R.color.white,
//                textColorResId = R.color.white,
            positiveClickListener = null,
            negativeClickListener = negativeClickListener,
            onCancelListener = onCancelListener,
            dialogTheme = R.style.Dialog,
            hasInput = true,
            inputValue = inputValue,
            hint = hint,
            onInputConfirmed = onInputConfirmed
        )
    }

    @SuppressLint("InflateParams")
    private fun createSimpleAlertDialog(
        context: Context,
        title: String? = null,
        message: String? = null,
        positiveButtonText: String? = null,
        negativeButtonText: String? = null,
        titleTextSize: Int,
        messageTextSize: Int,
        buttonsTextSize: Int,
        normalFontResId: Int? = null,
        semiBoldFontResId: Int? = null,
        boldFontResId: Int? = null,
        positiveColorResId: Int? = null,
        negativeColorResId: Int? = null,
        dialogTheme: Int? = null,
        positiveClickListener: DialogInterface.OnClickListener? = null,
        negativeClickListener: DialogInterface.OnClickListener? = null,
        onCancelListener: DialogInterface.OnCancelListener? = null,
        textColorResId: Int? = null,
        hasInput: Boolean = false,
        inputValue: String? = null,
        hint: String? = null,
        onInputConfirmed: ((input: String) -> Unit)? = null
    ): AlertDialog {
        val typefaceNormal =
            normalFontResId?.let { ResourcesCompat.getFont(context, normalFontResId) }
                ?: Typeface.DEFAULT
        val typefaceSemiBold =
            semiBoldFontResId?.let { ResourcesCompat.getFont(context, semiBoldFontResId) }
                ?: Typeface.DEFAULT
        val typefaceBold = boldFontResId?.let { ResourcesCompat.getFont(context, boldFontResId) }
            ?: Typeface.DEFAULT
//        val positiveColor = ContextCompat.getColor(context, positiveColorResId
//                ?: android.R.color.black)
//        val negativeColor = ContextCompat.getColor(context, negativeColorResId
//                ?: android.R.color.black)
        val textColor =
            textColorResId?.let { ResourcesCompat.getColor(context.resources, it, context.theme) }

        val titleSize = context.resources.getDimensionPixelSize(titleTextSize)
        val messageSize = context.resources.getDimensionPixelSize(messageTextSize)
        val buttonsSize = context.resources.getDimensionPixelSize(buttonsTextSize)

        val builder =
            dialogTheme?.let { AlertDialog.Builder(ContextThemeWrapper(context, dialogTheme)) }
                ?: AlertDialog.Builder(context)
        builder.setCancelable(true)

        var inputEditText: EditText? = null
        if (hasInput) {
            val inputLayout =
                LayoutInflater.from(context).inflate(R.layout.input_dialog_view, null, false)
                    .also { layout ->
                        inputEditText =
                            layout.findViewById<EditText>(R.id.input_dialog_edittext).also {
                                it.hint = hint
                                it.setText(inputValue)
                                it.focusAndShowKeyboard()
                            }
                    }
            builder.setView(inputLayout)
        }

        positiveButtonText?.let {
            builder.setPositiveButton(positiveButtonText.createFontSpannable(
                typefaceSemiBold,
                textSize = buttonsSize
            ),
                if (hasInput) DialogInterface.OnClickListener { _, _ ->
                    onInputConfirmed?.invoke(
                        inputEditText?.text.toString()
                    )
                } else positiveClickListener)
        }
        negativeButtonText?.let {
            builder.setNegativeButton(
                negativeButtonText.createFontSpannable(
                    typefaceSemiBold,
                    textSize = buttonsSize
                ), negativeClickListener
            )
        }
        title?.let {
            builder.setTitle(
                title.createFontSpannable(
                    typefaceBold,
                    textSize = titleSize
                )
            )
        }
        builder.setMessage(message?.createFontSpannable(typefaceNormal, textColor, messageSize))

        val dialog = builder.create()
//        dialog.setOnShowListener(DialogShownListener(dialog, positiveColor, negativeColor))
        onCancelListener?.let { dialog.setOnCancelListener(it) }

        inputEditText?.onImeAction(EditorInfo.IME_ACTION_DONE, function = {
            onInputConfirmed?.invoke(inputEditText?.text?.toString() ?: "")
            dialog.dismiss()
        })

        if (inputEditText != null) {
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }

        return dialog
    }

    private class DialogShownListener internal constructor(
        private val dialog: AlertDialog,
        private val positiveButtonColor: Int,
        private val negativeButtonColor: Int
    ) : DialogInterface.OnShowListener {
        override fun onShow(dialogInterface: DialogInterface) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(positiveButtonColor)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(negativeButtonColor)
        }
    }
}