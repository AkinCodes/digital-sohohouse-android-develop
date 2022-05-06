package com.sohohouse.seven.home.perks

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.databinding.PerksViewAllPerksBinding

const val PERKS_SEE_ALL_LAYOUT = R.layout.perks_view_all_perks

class LocalHousePerksSeeAllViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = PerksViewAllPerksBinding.bind(view)

    fun bind(onSeeAll: () -> Unit) {
        binding.perksCardSeeAll.clicks { onSeeAll() }
    }
}