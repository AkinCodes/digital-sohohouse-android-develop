package com.sohohouse.seven.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * IMPORTANT to cast the view holders as ViewHolder<T> in onCreateViewHolder
 */
abstract class GenericAdapter<T> : RecyclerView.Adapter<GenericAdapter.ViewHolder<T>>() {

    open var items: List<T> = listOf()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        holder.bind(items[position])
    }

    override fun onViewRecycled(holder: ViewHolder<T>) {
        holder.unbind()
    }

    protected fun inflateItemView(parent: ViewGroup, layoutRes: Int): View {
        return LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
    }

    abstract class ViewHolder<in T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
        open fun unbind() {}
    }
}