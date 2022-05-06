package com.sohohouse.seven.common.views.snackbar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView

class SnackbarTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var snackbarState: SnackbarState? = SnackbarState.DEFAULT

    fun setSnackbarState(state: SnackbarState) {
        if (snackbarState == state) return
        snackbarState = state
        refreshDrawableState()
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 3)
        View.mergeDrawableStates(drawableState, snackbarState?.value ?: intArrayOf())
        return drawableState
    }
}