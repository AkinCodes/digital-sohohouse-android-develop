package com.sohohouse.seven.profile.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.databinding.ItemEditProfileSectionHeaderBinding

class EditProfileSectionHeaderViewHolder(
    parent: ViewGroup,
    private val binding: ItemEditProfileSectionHeaderBinding =
        ItemEditProfileSectionHeaderBinding.bind(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_edit_profile_section_header, parent, false)
        )
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(sectionHeader: EditProfileAdapterItem.SectionHeader) = with(binding) {
        title.text = sectionHeader.title
        subtitle.text = sectionHeader.subtitle
    }
}
