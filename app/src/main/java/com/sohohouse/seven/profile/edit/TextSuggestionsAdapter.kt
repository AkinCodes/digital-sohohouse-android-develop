package com.sohohouse.seven.profile.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.base.BaseRecyclerDiffAdapter
import com.sohohouse.seven.databinding.ItemAutocompleteSuggestionBinding

class TextSuggestionsAdapter<T : AutoCompleteSuggestion>(private val listener: Listener<T>) :
    BaseRecyclerDiffAdapter<TextSuggestionViewHolder<T>, T>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextSuggestionViewHolder<T> {
        val binding = ItemAutocompleteSuggestionBinding.bind(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_autocomplete_suggestion, parent, false)
        )
        return TextSuggestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TextSuggestionViewHolder<T>, position: Int) {
        holder.bind(currentItems[position], listener)
    }

    interface Listener<T> {
        fun onSuggestionSelected(suggestion: T)
    }
}

class TextSuggestionViewHolder<T : AutoCompleteSuggestion>(
    private val binding: ItemAutocompleteSuggestionBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: T, listener: TextSuggestionsAdapter.Listener<T>) = with(binding) {
        autocompleteSuggestionTv.text = item.value
        root.setOnClickListener { listener.onSuggestionSelected(item) }
    }

}