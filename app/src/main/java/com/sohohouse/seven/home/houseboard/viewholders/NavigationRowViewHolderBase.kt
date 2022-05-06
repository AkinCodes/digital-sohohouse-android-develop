package com.sohohouse.seven.home.houseboard.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

abstract class NavigationRowViewHolderBase(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract val textView: TextView

    fun setOnClickListener(onClickListener: View.OnClickListener) {
        textView.setOnClickListener(onClickListener)
    }

    fun setText(text: String?) {
        textView.text = text
    }
}