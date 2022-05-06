package com.sohohouse.seven.common.viewholders

import android.view.View
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.book.adapter.model.DividerBookAdapterItem
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.extensions.context
import com.sohohouse.seven.databinding.ListDividerLayoutBinding
import com.sohohouse.seven.planner.PlannerHeaderAdapterItem

const val LIST_DIVIDER_LAYOUT = R.layout.list_divider_layout

class ListSectionHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val binding = ListDividerLayoutBinding.bind(view)

    fun setText(@StringRes resString: Int) {
        binding.label.setText(resString)
    }

    fun bind(adapterItem: DividerBookAdapterItem) {
        if (adapterItem.titleRes != null) {
            setText(adapterItem.titleRes)
        }
    }

    fun bind(adapterItem: PlannerHeaderAdapterItem) {
        setText(adapterItem.stringRes)
        binding.label.contentDescription = context.getString(adapterItem.stringRes)
    }

    fun bind(adapterItem: BaseAdapterItem.HouseNoteItem.SubHeader) {
        setText(adapterItem.titleRes)
    }
}