package com.sohohouse.seven.home.suggested_people

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.SuggestedPeopleBinding

class SuggestedCarouselViewHolder(private val binding: SuggestedPeopleBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: SuggestedAdapterItem,
        recyclerItemCallback: (userID: String) -> Unit,
        seeAllCallback: (View) -> Unit,
        optInCallback: (View) -> Unit,
    ) {
        with(binding) {

            if (item.suggestedMembers.isEmpty()) {
                comeBackSoonView.isVisible = true
                suggestions.isVisible = false
                list.adapter = null
            } else {
                comeBackSoonView.isVisible = false
                suggestions.isVisible = true
                list.adapter = SuggestedPeopleAdapter(recyclerItemCallback, optInCallback).apply {
                    submitList(item.suggestedMembers.take(MAX_PEOPLE_IN_CAROUSEL))
                }
            }

            if (item.suggestedMembers.size > MAX_PEOPLE_IN_CAROUSEL) {
                suggestionsButton.isVisible = true
                suggestionsButton.setOnClickListener(seeAllCallback)
                suggestionsButton.setText(R.string.see_all)
            } else if (!item.isOptedInForSuggestions) {
                suggestionsButton.isVisible = true
                suggestionsButton.setOnClickListener(optInCallback)
                suggestionsButton.setText(R.string.opt_in)
            } else {
                suggestionsButton.isVisible = false
            }
        }
    }

    companion object {
        private const val MAX_PEOPLE_IN_CAROUSEL = 11
    }
}

