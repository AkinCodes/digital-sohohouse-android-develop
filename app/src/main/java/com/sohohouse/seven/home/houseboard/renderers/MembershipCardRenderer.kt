package com.sohohouse.seven.home.houseboard.renderers

import android.view.ViewGroup
import ca.symbilityintersect.rendereradapter.Renderer
import com.sohohouse.seven.home.houseboard.items.MembershipCardItem
import com.sohohouse.seven.home.houseboard.viewholders.MembershipCardViewHolder
import java.lang.reflect.Type

class MembershipCardRenderer : Renderer<MembershipCardItem, MembershipCardViewHolder> {
    override fun bindViewHolder(item: MembershipCardItem, holder: MembershipCardViewHolder) {
        holder.bind(item)
    }

    override fun getType(): Type {
        return MembershipCardItem::class.java
    }

    override fun createViewHolder(parent: ViewGroup): MembershipCardViewHolder {
        return MembershipCardViewHolder(parent)
    }

}