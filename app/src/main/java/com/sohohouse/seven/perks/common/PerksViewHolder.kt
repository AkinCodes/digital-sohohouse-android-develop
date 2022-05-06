package com.sohohouse.seven.perks.common

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.databinding.ViewPerksSingleCardBinding
import com.sohohouse.seven.network.core.models.Perk
import com.sohohouse.seven.perks.common.enums.HouseRegionFilter

const val PERKS_LAYOUT = R.layout.view_perks_single_card

class PerksViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(
        item: Perk,
        associatedVenueName: String = "",
        onItemClick: (perkId: String, sharedImageView: ImageView) -> Unit
    ) {
        with(ViewPerksSingleCardBinding.bind(itemView)) {
            perksCardHeaderImage.setImageFromUrl(item.headerImageLarge)

            val filterRegionList = HouseRegionFilter.values().filter { it.id == item.region }

            val regionText = filterRegionList.takeIf { it.isNotEmpty() }
                ?.let { getString(it.first().resourceString) }
                .orEmpty()

            perksCardRegion.text = if (associatedVenueName.isEmpty()) {
                regionText
            } else {
                getString(R.string.perks_region_venue_label).replaceBraces(
                    regionText,
                    associatedVenueName
                )
            }

            perksCardTitle.text = item.title.orEmpty()

            perksCardDescription.visibility =
                if (item.expiresOn == null) View.INVISIBLE else View.VISIBLE
            item.expiresOn?.let {
                val attrString = getString(R.string.perks_expires_label)
                perksCardDescription.text = attrString.replaceBraces((it.getFormattedDate()))
            }

            itemView.clicks { onItemClick(item.id, perksCardHeaderImage) }
        }

    }
}