package com.sohohouse.seven.profile.edit

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.databinding.ItemEditProfilePrivateInfoHeaderBinding

class EditProfilePrivateInfoViewHolder(
    parent: ViewGroup,
    private val listener: EditProfileListener,
    binding: ItemEditProfilePrivateInfoHeaderBinding =
        ItemEditProfilePrivateInfoHeaderBinding.bind(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_edit_profile_private_info_header, parent, false)
        )
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.editProfileContactUs.clicks {
            listener.onContactUsClick()
        }
    }

}