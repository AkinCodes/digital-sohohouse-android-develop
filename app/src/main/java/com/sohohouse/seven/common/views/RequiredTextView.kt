package com.sohohouse.seven.common.views

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import android.util.AttributeSet
import com.sohohouse.seven.R


class RequiredTextView(context: Context, attrs: AttributeSet?) : AppCompatTextView(context, attrs) {
    override fun setText(text: CharSequence?, type: BufferType?) {
        if (text?.isEmpty() == true) {
            super.setText(context.getString(R.string.fe_generic_error), type)
            return
        }
        super.setText(text, type)
    }
}