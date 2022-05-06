package com.sohohouse.seven.profile.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.common.views.CircleOutlineProvider
import com.sohohouse.seven.databinding.ItemEditProfileHeaderBinding

class EditProfileHeaderViewHolder(
    parent: ViewGroup,
    private val listener: EditProfileListener,
    private val binding: ItemEditProfileHeaderBinding =
        ItemEditProfileHeaderBinding.bind(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_edit_profile_header, parent, false)
        )
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        with(binding) {
            editProfileAvatar.outlineProvider = CircleOutlineProvider()
            editProfileAvatar.clipToOutline = true
        }
    }

    fun bind(item: EditProfileAdapterItem.Header) = with(binding) {
        editProfileAvatar.setImageFromUrl(
            item.imageUrl,
            placeholder = R.drawable.add_profile_pic,
            isRound = true
        )
        editProfileName.text = item.name

        editProfileAvatar.clicks {
            listener.onEditPhotoClick()
        }
    }
}
