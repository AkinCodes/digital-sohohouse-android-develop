package com.sohohouse.seven.profile.view.viewholder

import android.content.res.ColorStateList
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.databinding.ViewHolderProfileHeaderBinding
import com.sohohouse.seven.profile.NotAvailableMySelf
import com.sohohouse.seven.profile.view.renderer.ProfileHeaderRenderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class ProfileHeaderViewHolder(
    private val binding: ViewHolderProfileHeaderBinding,
    private val coroutineScope: Lazy<CoroutineScope>,
    private val onProfileAvatarClick: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ProfileHeaderRenderer.ProfileHeaderItem) = with(binding) {
        profileAvatar.setImageUrl(
            item.profileItem.imageUrl,
            if (NotAvailableMySelf == item.profileItem.status) R.drawable.add_profile_pic_large else R.drawable.ic_profile
        )
        profileName.text = item.profileItem.fullName
        staffIndication.isVisible = item.profileItem.isStaff
        profilePronouns.setTextOrHide(item.profileItem.pronouns?.getPublicDisplayValue(context.stringProvider))
        profileCity.setTextOrHide(item.profileItem.location)
        profileOccupation.setTextOrHide(item.profileItem.occupation)
        profileAvatar.setOnClickListener {
            onProfileAvatarClick()
        }

        item.userAvailableStatusColorAttrRes.onStart {
            availabilityBackground.setGone()
            availabilityImage.setGone()
        }.onEach { (isCheckedIn, colorAttrRes) ->
            availabilityBackground.isVisible = isCheckedIn
            availabilityImage.isVisible = isCheckedIn

            availabilityImage.backgroundTintList = ColorStateList.valueOf(
                availabilityImage.getAttributeColor(colorAttrRes)
            )
        }.launchIn(coroutineScope.value)
    }

}