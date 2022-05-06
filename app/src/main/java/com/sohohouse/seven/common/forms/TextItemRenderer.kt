package com.sohohouse.seven.common.forms

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import android.view.View
import ca.symbilityintersect.rendereradapter.BaseRenderer


class TextItemRenderer(
    @param:LayoutRes @field:LayoutRes
    private val mTextInputLayoutId: Int, @param:IdRes @field:IdRes
    private val mTextInputResId: Int
) : BaseRenderer<TextItem, TextViewHolder>(TextItem::class.java) {

    override fun getLayoutResId(): Int {
        return this.mTextInputLayoutId
    }

    override fun createViewHolder(itemView: View): TextViewHolder {
        return TextViewHolder(this.mTextInputResId, itemView)
    }

    override fun bindViewHolder(item: TextItem, holder: TextViewHolder) {
        holder.populate(item)
    }
}