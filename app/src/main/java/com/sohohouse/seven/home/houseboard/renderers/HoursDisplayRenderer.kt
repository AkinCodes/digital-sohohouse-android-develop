package com.sohohouse.seven.home.houseboard.renderers

import android.view.View
import ca.symbilityintersect.rendereradapter.BaseRenderer
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.ItemHoursDisplayBinding
import com.sohohouse.seven.home.houseboard.items.HoursDisplayItem
import com.sohohouse.seven.home.houseboard.viewholders.HoursDisplayViewHolder

class HoursDisplayRenderer(
    val onViewMembershipCardClick: () -> Unit,
    val onLocalHouseClick: () -> Unit
) : BaseRenderer<HoursDisplayItem, HoursDisplayViewHolder>(HoursDisplayItem::class.java) {

    override fun bindViewHolder(item: HoursDisplayItem?, holder: HoursDisplayViewHolder?) {
        holder?.run {
            setVenueName(item?.venueName)
            setTopLabel(item?.topText ?: "")
            setBottomLabel(item?.bottomText ?: "")
            setButtonText(item?.buttonText ?: "")
            setOnButtonClickListener {
                if (item != null) {
                    onViewMembershipCardClick()
                }
            }
            setVenueNameClickListener {
                if (item != null) {
                    onLocalHouseClick()
                }
            }
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.item_hours_display
    }

    override fun createViewHolder(view: View): HoursDisplayViewHolder {
        return HoursDisplayViewHolder(ItemHoursDisplayBinding.bind(view))
    }

}