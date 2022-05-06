package com.sohohouse.seven.home.houseboard.renderers

import android.view.View
import ca.symbilityintersect.rendereradapter.BaseRenderer
import com.sohohouse.seven.databinding.ItemNavigationRowBinding
import com.sohohouse.seven.home.houseboard.items.NavigationRowItem
import com.sohohouse.seven.home.houseboard.viewholders.MEDIUM_NAVIGATION_ROW_LAYOUT
import com.sohohouse.seven.home.houseboard.viewholders.NavigationRowViewHolder

class NavigationRowRenderer(val clickListener: (NavigationRowItem) -> Unit) :
    BaseRenderer<NavigationRowItem, NavigationRowViewHolder>(NavigationRowItem::class.java) {

    override fun bindViewHolder(item: NavigationRowItem?, holder: NavigationRowViewHolder?) {
        holder?.apply {
            setText(item?.text ?: "")
            setOnClickListener {
                if (item != null) {
                    clickListener(item)
                }
            }
        }
    }

    override fun getLayoutResId(): Int = MEDIUM_NAVIGATION_ROW_LAYOUT

    override fun createViewHolder(view: View): NavigationRowViewHolder =
        NavigationRowViewHolder(ItemNavigationRowBinding.bind(view))

}