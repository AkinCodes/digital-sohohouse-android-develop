package com.sohohouse.seven.home.adapter.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.R
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.databinding.ItemHomeContentSectionHeaderBinding
import com.sohohouse.seven.databinding.LocalHouseSectionHeaderLayoutBinding

const val HOUSE_LANDING_SECTION_HEADER_LAYOUT = R.layout.house_landing_section_header_layout
const val LOCAL_HOUSE_SECTION_HEADER_LAYOUT = R.layout.local_house_section_header_layout

class HouseNotesSectionHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private var homeContentBinding: ItemHomeContentSectionHeaderBinding? = null

    //This should not be here most likely but leaving not to break existing functionality
    @Deprecated("Remove for future (needs more investigation)")
    private var localHouseBinding: LocalHouseSectionHeaderLayoutBinding? = null

    init {
        try {
            homeContentBinding = ItemHomeContentSectionHeaderBinding.bind(view)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
        homeContentBinding?.let {
            it.title.setText(R.string.home_house_notes_header)
            it.subtitle.setText(R.string.home_house_notes_subheader)
        }

        try {
            localHouseBinding = LocalHouseSectionHeaderLayoutBinding.bind(view)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun bind(listener: () -> Unit = {}) {
        homeContentBinding?.let {
            it.title.setText(R.string.home_house_notes_header)
            it.subtitle.setText(R.string.home_house_notes_subheader)
            it.seeAll.setOnClickListener { BaseAdapterItem.HouseNoteItem.HouseNotesHeader.listener?.onHouseNotesSeeAllClick() }
        }
    }

    fun bind(label: Int) {
        localHouseBinding?.sectionLabel?.setText(label)
    }

}