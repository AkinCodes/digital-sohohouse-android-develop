package com.sohohouse.seven.common.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.sohohouse.seven.R

open class FormView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttrs) {

    init {
        showDividers = SHOW_DIVIDER_MIDDLE
        dividerDrawable = ContextCompat.getDrawable(context, R.drawable.form_divider)
        orientation = VERTICAL
        setBackgroundResource(R.drawable.form_background)
    }

}