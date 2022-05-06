package com.sohohouse.seven.common.design.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

open class RendererAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected var items: MutableList<T> = mutableListOf()

    private val renderers: HashMap<Int, Renderer<T, RecyclerView.ViewHolder>> = hashMapOf()

    @SuppressLint("NotifyDataSetChanged")
    open fun submitItems(items: List<T>) {
        this.items = items.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position) ?: return -1
        val viewType = item::class.java.hashCode()
        if (renderers[viewType] == null) {
            throw Exception("No renderer found : ${item::class.java.name}")
        }
        return viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return renderers[viewType]?.createViewHolder(parent)
            ?: throw Exception("Unknown view type: $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        renderers[getItemViewType(position)]?.bindViewHolder(holder, getItem(position) ?: return)
    }

    private fun getItem(position: Int): T? {
        return if (position in 0..items.size) {
            items[position]
        } else {
            null
        }
    }

    fun registerRenderers(vararg renderers: Renderer<*, *>) {
        for (renderer in renderers) {
            registerRenderer(renderer)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun registerRenderer(renderer: Renderer<*, *>) {
        try {
            renderers[renderer.type.hashCode()] = renderer as Renderer<T, RecyclerView.ViewHolder>
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}