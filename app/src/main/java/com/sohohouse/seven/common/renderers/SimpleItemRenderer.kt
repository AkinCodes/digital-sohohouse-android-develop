package com.sohohouse.seven.common.renderers

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import ca.symbilityintersect.rendereradapter.BaseRenderer

class SimpleViewHolder(view: View) : RecyclerView.ViewHolder(view)

abstract class SimpleRenderer<T>(clazz: Class<T>) :
    BaseRenderer<T, RecyclerView.ViewHolder>(clazz) {
    override fun createViewHolder(view: View) = SimpleViewHolder(view)
}


class SimpleBindingViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

abstract class SimpleBindingRenderer<T>(clazz: Class<T>) :
    BaseRenderer<T, SimpleBindingViewHolder>(clazz)