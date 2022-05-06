package com.sohohouse.seven.common.views

import android.content.Context
import androidx.appcompat.widget.AppCompatSpinner
import android.util.AttributeSet

class EnquirySpinner : AppCompatSpinner {

    constructor(
        context: Context, attrs: AttributeSet,
        defStyle: Int
    ) : super(context, attrs, defStyle)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context) : super(context)

    var shouldScrollToTop = true
    private var toggle = false

    override fun getSelectedItemPosition(): Int {
        // this toggle is required because this method will get called in other
        // places too, the most important being called for the
        // OnItemSelectedListener
        return if (toggle)
            0
        else super.getSelectedItemPosition()
    }

    override fun performClick(): Boolean {
        // this method shows the list of elements from which to select one.
        // we have to make the getSelectedItemPosition to return 0 so we can
        // fool the Spinner and let it think that the selected item is the first
        // element
        if (shouldScrollToTop) {
            toggle = true
        }
        val value = super.performClick()
        toggle = false
        return value
    }
}