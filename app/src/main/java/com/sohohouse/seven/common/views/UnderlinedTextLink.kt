package com.sohohouse.seven.common.views

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.AttributeSet

class UnderlinedTextLink @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttrs) {

    override fun setText(text: CharSequence?, type: BufferType?) {
        val content =
            SpannableString(text.toString()).apply { setSpan(UnderlineSpan(), 0, length, 0) }
        super.setText(content, type)
    }
}