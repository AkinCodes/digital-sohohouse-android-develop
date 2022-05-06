package com.sohohouse.seven.common.design.adapter

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.base.DefaultDiffItemCallback
import com.sohohouse.seven.base.DiffItem
import java.util.*

open class PagedRendererAdapter<T : DiffItem> :
    PagedListAdapter<T, RecyclerView.ViewHolder>(DefaultDiffItemCallback()) {

    private val renderers: HashMap<Int, Renderer<T, RecyclerView.ViewHolder>> = hashMapOf()

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