package com.sohohouse.seven.home.housenotes.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.databinding.LocalHouseHouseNotePersonalizeLayoutBinding

const val HOUSE_NOTE_PERSONALIZE_LAYOUT = R.layout.local_house_house_note_personalize_layout

class LocalHouseHouseNoteButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = LocalHouseHouseNotePersonalizeLayoutBinding.bind(view)

    fun setButtonClickListener(onNext: (Any) -> Unit) {
        binding.personalizeBtn.clicks(onNext)
    }
}