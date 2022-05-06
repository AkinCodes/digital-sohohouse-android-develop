package com.sohohouse.seven.common.views.locationlist

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.common.house.HouseRegion

abstract class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        private const val ICON_CLOSED_DEGREES = 180f
        private const val ICON_OPEN_DEGREES = 0f
        private const val OPENING_PARENTHESIS = " ("
        private const val CLOSING_PARENTHESIS = ")"
    }

    protected abstract val header: TextView
    protected abstract val icon: ImageView

    fun onBind(item: LocationRecyclerParentItem, showSelectedCount: Boolean) {
        val headerName = getString(item.region.stringRes)
        header.text = if (showSelectedCount && item.selectedChildCount != 0) {
            headerName.plus(OPENING_PARENTHESIS).plus(item.selectedChildCount)
                .plus(CLOSING_PARENTHESIS)
        } else {
            headerName
        }

        icon.setImageResource(R.drawable.disclosure)
        icon.rotation = if (item.expanded) ICON_CLOSED_DEGREES else ICON_OPEN_DEGREES
    }

    fun setupOnClick(
        model: LocationRecyclerParentItem,
        onClickListener: (region: HouseRegion) -> (Unit)
    ) {
        itemView.clicks {
            onClickListener.invoke(model.region)
            icon.rotation = if (model.expanded) ICON_CLOSED_DEGREES else ICON_OPEN_DEGREES
        }
    }
}