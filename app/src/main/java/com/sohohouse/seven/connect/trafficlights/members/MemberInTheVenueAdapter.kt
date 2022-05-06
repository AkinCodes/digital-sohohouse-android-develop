package com.sohohouse.seven.connect.trafficlights.members

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DefaultDiffItemCallback
import com.sohohouse.seven.common.extensions.getAttributeColor
import com.sohohouse.seven.common.utils.fastBlur
import com.sohohouse.seven.connect.VenueMember
import com.sohohouse.seven.databinding.MemberInTheVenueItemBinding
import com.sohohouse.seven.profile.RequestSent

class MemberInTheVenueAdapter(
    private val onMemberProfileClick: (VenueMember) -> Unit
) : PagedListAdapter<MemberInTheVenueListItem, MemberInTheVenueAdapter.VenueItem>(
    DefaultDiffItemCallback()
) {

    class VenueItem(val binding: MemberInTheVenueItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueItem {
        val binding = MemberInTheVenueItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VenueItem(binding)
    }

    override fun onBindViewHolder(holder: VenueItem, position: Int) {
        val memberInTheVenueListItem = getItem(position) as MemberInTheVenueListItem
        val venueMember = memberInTheVenueListItem.venueMember

        holder.binding.apply {
            skeletonGroup.isInvisible = !memberInTheVenueListItem.isBlurred
            infoGroup.isInvisible = memberInTheVenueListItem.isBlurred

            image.setImageUrl(
                venueMember.imageUrl,
                R.drawable.ic_member_placeholder_dark
            ) {
                if (memberInTheVenueListItem.isBlurred) it.fastBlur(0.1F, 20) else it
            }

            setUpButton(venueMember, memberInTheVenueListItem)

            title.text = venueMember.fullName
            subtitle.text = venueMember.occupation
            imageStatus.backgroundTintList = if (venueMember.isConnection)
                ColorStateList.valueOf(root.getAttributeColor(R.attr.colorTrafficLightConnectionsOnly))
            else
                ColorStateList.valueOf(root.getAttributeColor(R.attr.colorTrafficLightAvailable))
        }

    }

    private fun MemberInTheVenueItemBinding.setUpButton(
        venueMember: VenueMember,
        memberInTheVenueListItem: MemberInTheVenueListItem
    ) {

        connect.setOnClickListener { memberInTheVenueListItem.onActionRequest() }

        if (memberInTheVenueListItem.button.buttonTitle != 0)
            connect.setText(memberInTheVenueListItem.button.buttonTitle)
        else
            connect.text = ""

        connect.isVisible = memberInTheVenueListItem.button.buttonVisible

        connect.setBackgroundResource(
            if (venueMember.mutualConnectionStatus is RequestSent)
                R.drawable.button_secondary
            else
                R.drawable.button_secondary_dark_filled
        )

        val textColorRes = if (venueMember.mutualConnectionStatus is RequestSent)
            R.attr.colorButtonPrimaryText
        else
            R.attr.colorButtonSecondaryText

        connect.setTextColor(root.getAttributeColor(textColorRes))
        image.setOnClickListener {
            onMemberProfileClick(venueMember)
        }
    }

}
