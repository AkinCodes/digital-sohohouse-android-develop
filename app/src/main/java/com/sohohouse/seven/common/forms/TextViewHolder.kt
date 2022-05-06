package com.sohohouse.seven.common.forms

import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView

class TextViewHolder(@IdRes layoutResId: Int, itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val mTextField: TextView?

    init {
        this.mTextField = itemView.findViewById<View>(layoutResId) as TextView
    }

    fun populate(item: TextItem) {
        mTextField?.text = item.displayText
    }
}
