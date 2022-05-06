package com.sohohouse.seven.browsehouses.recycler

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.adapterhelpers.BaseAdapterItem
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.databinding.BrowseHousesHouseItemBinding
import com.sohohouse.seven.network.core.models.Venue


class BrowseAllHouseViewHolder(private val binding: BrowseHousesHouseItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: BaseAdapterItem.BrowseHousesItem.Content, onClick: (venue: Venue) -> Unit) = with(binding) {
        browseHousesName.text = item.house.name
        root.clicks {
            onClick(item.house)
        }
    }

    fun iconVisibility(isVisible: Boolean) = with(binding) {
        browseHousesForwardIcon.alpha = if (isVisible) 0f else 1f
        val newAlpha = if (isVisible) 1f else 0f
        val newVisibility = if (isVisible) View.VISIBLE else View.INVISIBLE
        browseHousesForwardIcon.animate()
            .alpha(newAlpha)
            .setDuration(100)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    super.onAnimationStart(animation)
                    browseHousesForwardIcon.visibility = newVisibility
                }
            })
    }
}