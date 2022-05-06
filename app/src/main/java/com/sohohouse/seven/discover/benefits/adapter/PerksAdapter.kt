package com.sohohouse.seven.discover.benefits.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DefaultDiffItemCallback
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.databinding.ViewPerksSingleCardBinding

class PerksAdapter(private val listener: (String, String?, String?) -> Unit) :
    PagedListAdapter<PerksItem, PerksAdapter.PerksViewHolder>(DefaultDiffItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerksViewHolder {
        return PerksViewHolder(
            ViewPerksSingleCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PerksViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    inner class PerksViewHolder(val binding: ViewPerksSingleCardBinding) :
        ViewHolder(binding.root) {

        fun bind(item: PerksItem) = with(binding) {
            root.setOnClickListener {
                listener(item.id, item.title, item.promoCode)
            }

            perksCardTitle.text = item.title
            perksCardHeaderImage.setImageFromUrl(item.imageUrl)

            perksCardDescription.visibility = if (item.description.isNullOrBlank()) {
                View.GONE
            } else {
                perksCardDescription.text = item.description
                View.VISIBLE
            }

            perksCardRegion.text = getPerksCardRegionText(item)

            perksCardExpiry.visibility = if (item.expiry == null) {
                View.INVISIBLE
            } else {
                perksCardExpiry.text = context.getString(R.string.perks_expires_label)
                    .replaceBraces((item.expiry.getFormattedDate()))
                View.VISIBLE
            }
        }

        private fun getPerksCardRegionText(item: PerksItem): String {
            if (item.contentPillar != null && item.city != null) {
                return context.getString(
                    R.string.benefit_city_and_content_pillar,
                    item.city,
                    context.getString(item.contentPillar)
                )
            } else if (item.city != null) {
                return item.city
            } else if (item.contentPillar != null) {
                return context.getString(item.contentPillar)
            }
            return ""
        }
    }

}

