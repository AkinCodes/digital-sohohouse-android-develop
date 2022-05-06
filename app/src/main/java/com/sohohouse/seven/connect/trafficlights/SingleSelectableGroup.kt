package com.sohohouse.seven.connect.trafficlights

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group

class SingleSelectableGroup @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Group(context, attrs, defStyleAttr) {

    fun setOnClickListener(onClick: (viewId: Int) -> Unit) {
        referencedIds()
            .map { parentConstraintLayout().findViewById<View>(it) }
            .forEach { view -> setOnClickListener(view, onClick) }
    }

    private fun setOnClickListener(view: View, onClick: (viewId: Int) -> Unit) {
        view.setOnClickListener { clickedView ->
            if (!clickedView.isSelected) {
                clickedView.isSelected = true
                referencedIds().filter {
                    it != clickedView.id
                }.forEach {
                    parentConstraintLayout().findViewById<View>(it).isSelected = false
                }
                onClick(clickedView.id)
            }
        }
    }

    private fun referencedIds() = mIds.take(mCount)

    private fun parentConstraintLayout() = parent as? ConstraintLayout
        ?: error("Parent of SelectableGroup must be ConstraintLayout")

}