package com.sohohouse.seven.common.views

import android.content.Context
import androidx.core.widget.TextViewCompat
import androidx.appcompat.widget.AppCompatTextView
import android.util.AttributeSet
import com.sohohouse.seven.R

class HouseNameTextView @JvmOverloads constructor(
    con: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    AppCompatTextView(con, attrs, defStyleAttr) {

    init {
        //If attribute is not set use default value
        if (attrs?.styleAttribute == 0) {
            TextViewCompat.setTextAppearance(this, R.style.EventCardHouseName)
        }
    }

}