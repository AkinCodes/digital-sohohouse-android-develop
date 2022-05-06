package com.sohohouse.seven.housepay.checkdetail.render.viewholder

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.layoutInflater
import com.sohohouse.seven.databinding.ItemCheckLineNoteBinding

class CheckLineNoteViewHolder(
    private val binding: ItemCheckLineNoteBinding
) : RecyclerView.ViewHolder(
    binding.root
) {
    fun bind(text: String) {
        binding.root.text = text
    }

    companion object {
        fun create(parent: ViewGroup): CheckLineNoteViewHolder {
            return CheckLineNoteViewHolder(
                ItemCheckLineNoteBinding.inflate(
                    parent.layoutInflater(),
                    parent,
                    false
                )
            )
        }
    }

}
