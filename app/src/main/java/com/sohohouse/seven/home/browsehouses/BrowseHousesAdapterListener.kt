package com.sohohouse.seven.home.browsehouses

import android.widget.ImageView
import com.sohohouse.seven.network.core.models.Venue

interface BrowseHousesAdapterListener {
    fun onHomeClicked(venueID: String, sharedImageView: ImageView, venue: Venue)
}

interface BrowseHousesViewSizeListener {
    fun setBackgroundImage(imageUrl: String?)
    fun onHomeClicked(venue: Venue, position: Int)
}