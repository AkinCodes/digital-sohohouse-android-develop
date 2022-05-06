package com.sohohouse.seven.common.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.view.View
import com.sohohouse.seven.R
import com.sohohouse.seven.common.utils.CurrencyUtils
import java.lang.IllegalArgumentException
import java.util.regex.Matcher
import java.util.regex.Pattern

@Deprecated(
    message = "Use String.isNullOrEmpty()",
    replaceWith = ReplaceWith("String.isNullOrEmpty")
)
fun String?.isStringEmpty(): Boolean {
    return this == null || this.isEmpty()
}

fun String?.requiresErrorMessage(context: Context): String {
    return if (this.isNullOrEmpty()) {
        return context.getString(R.string.fe_generic_error)
    } else {
        this
    }
}

fun String.copyToClipboard(context: Context, label: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, this)
    clipboard.setPrimaryClip(clip)
}

fun String.createFontSpannable(
    typeface: Typeface,
    color: Int? = null,
    textSize: Int? = null
): SpannableStringBuilder {
    val sb = SpannableStringBuilder(this)
    sb.setSpan(CustomTFSpan(typeface), 0, this.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    color?.let {
        sb.setSpan(
            ForegroundColorSpan(color),
            0,
            this.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    textSize?.let {
        sb.setSpan(
            AbsoluteSizeSpan(it),
            0,
            this.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return sb
}

fun String.createFontSpannableForSubstring(
    typeface: Typeface?,
    color: Int? = null,
    vararg values: String
): CharSequence {
    if (typeface == null) return this
    val sb = SpannableStringBuilder(this)
    for (value in values) {
        val index = this.indexOf(value)
        if (index != -1) {
            sb.setSpan(
                CustomTFSpan(typeface),
                index,
                index + value.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            color?.let {
                sb.setSpan(
                    ForegroundColorSpan(color),
                    index,
                    index + values.size,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }
    return sb
}

fun CharSequence.createClickableSpannableForSubstring(
    value: String,
    onClick: () -> Unit,
    theme: Resources.Theme? = null,
    bold: Boolean = true,
    underline: Boolean = false
): CharSequence {
    if (!this.contains(value)) return this
    val sb = SpannableStringBuilder(this)
    val index = this.indexOf(value)
    if (index != -1) {
        sb.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                onClick()
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = underline
                ds.isFakeBoldText = bold
                theme?.let { ds.color = it.getAttributeColor(R.attr.colorTextBody014Accent) }
            }
        }, index, index + value.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return sb
}

fun String.replaceBraces(vararg values: String): String {
    val pattern = Pattern.compile("\\{[A-Z a-z0-9]+\\}")
    val matcher = pattern.matcher(this)
    val stringBuffer = StringBuffer()
    var valueIdx = 0

    while (matcher.find()) {
        matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(values[valueIdx]))
        valueIdx++
    }

    matcher.appendTail(stringBuffer)
    return stringBuffer.toString()
}

fun String.removeBuildSuffix(): String {
    val index = this.indexOf("-")
    if (index >= 0) {
        return this.removeRange(index..this.lastIndex)
    }
    return this
}

fun String?.nullIfBlank(): String? = if (this.isNullOrBlank()) null else this

fun String?.parseColor(): Int? {
    return try {
        Color.parseColor(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}

fun String.lowerCaseEveryCharExceptFirst() =
    this.substring(0, 1) + this.substring(1, this.length).lowercase()


class CustomTFSpan(private val customTypeface: Typeface) : TypefaceSpan("") {

    override fun updateDrawState(ds: TextPaint) {
        applyTypeFace(ds, customTypeface)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyTypeFace(paint, customTypeface)
    }

    private fun applyTypeFace(paint: Paint, tf: Typeface) {
        paint.typeface = tf
    }
}

fun String?.isNotEmpty(): Boolean {
    return isNullOrEmpty().not()
}

fun String.insert(index: Int, toInsert: String): String {
    return StringBuilder(this).insert(index, toInsert).toString()
}

fun String.prepend(toInsert: String): String {
    return insert(0, toInsert)
}

//Gets number of decimal places
fun String.getPrecision(): Int {
    this.toFloatOrNull() ?: return 0
    val n = split(".")
    return if (n.size > 1) n[1].length else 0
}