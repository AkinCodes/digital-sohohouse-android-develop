package com.sohohouse.seven.home.houseboard.viewholders

import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

abstract class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract val button: Button

    fun setButtonText(text: String) {
        button.text = text
    }

    fun setOnClickListener(onClickListener: View.OnClickListener) {
        button.setOnClickListener(onClickListener)
    }
}