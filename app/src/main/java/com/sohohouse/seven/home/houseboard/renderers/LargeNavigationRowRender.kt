package com.sohohouse.seven.home.houseboard.renderers

import android.view.View
import ca.symbilityintersect.rendereradapter.BaseRenderer
import com.sohohouse.seven.databinding.ItemLargeNavigationRowBinding
import com.sohohouse.seven.home.houseboard.items.LargeNavigationRowItem
import com.sohohouse.seven.home.houseboard.viewholders.LARGE_NAVIGATION_ROW_LAYOUT
import com.sohohouse.seven.home.houseboard.viewholders.LargeNavigationRowViewHolder

class LargeNavigationRowRender :
    BaseRenderer<LargeNavigationRowItem, LargeNavigationRowViewHolder>(LargeNavigationRowItem::class.java) {

    override fun bindViewHolder(
        item: LargeNavigationRowItem?,
        holder: LargeNavigationRowViewHolder?
    ) {
        holder?.apply {
            setText(item?.text)
            textView.contentDescription = item?.contentDescription
            setOnClickListener {
                item?.clickListener?.invoke()
            }
        }
    }

    override fun getLayoutResId(): Int = LARGE_NAVIGATION_ROW_LAYOUT

    override fun createViewHolder(view: View): LargeNavigationRowViewHolder =
        LargeNavigationRowViewHolder(ItemLargeNavigationRowBinding.bind(view))
}