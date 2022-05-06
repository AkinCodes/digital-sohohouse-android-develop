package com.sohohouse.seven.common.design.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView

interface Renderer<M, VH : RecyclerView.ViewHolder> {

    val type: Class<M>

    fun createViewHolder(@NonNull parent: ViewGroup): VH

    fun bindViewHolder(holder: VH, item: M)

    fun createItemView(parent: ViewGroup, @LayoutRes layoutRes: Int): View {
        return LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
    }
}