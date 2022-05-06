package com.sohohouse.seven.apponboarding.housepreferences

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.views.locationlist.*
import com.sohohouse.seven.databinding.FilterHeaderExtraSpacingBinding

class OnboardingLocationAdapter(
    allHouses: List<LocationRecyclerParentItem>,
    favouriteHouses: List<LocationRecyclerChildItem>,
    selectionChangedListener: SelectedLocationListener
) : BaseLocationRecyclerAdapter(
    favouriteHouses,
    allHouses,
    locationClickListener(selectionChangedListener)
) {

    init {
        add(
            LocationRecyclerTextItem(
                headerStringRes = R.string.more_house_settings_cta,
                itemType = FilterItemType.SUBHEADER
            )
        )
        addMyHouses()

        add(
            LocationRecyclerTextItem(
                headerStringRes = R.string.explore_events_filter_all_houses_label,
                subtitleStringRes = R.string.app_onboarding_houses_add_houses_supporting,
                itemType = FilterItemType.HEADER
            )
        )

        addAllHouses()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (FilterItemType.values()[viewType]) {
            FilterItemType.HEADER -> LocationComponentViewHolder(
                FilterHeaderExtraSpacingBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> super.onCreateViewHolder(parent, viewType)
        }
    }
}

private fun locationClickListener(selectionChangedListener: SelectedLocationListener) =
    object : LocationClickListener {
        override fun onLocationClicked(selectedLocations: List<String>) {
            selectionChangedListener.onSelectedLocationsChanged(selectedLocations)
        }
    }