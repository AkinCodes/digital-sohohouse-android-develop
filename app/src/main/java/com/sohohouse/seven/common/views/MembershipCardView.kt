package com.sohohouse.seven.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ViewSwitcher
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.common.extensions.setVisible
import com.sohohouse.seven.common.user.SubscriptionType
import com.sohohouse.seven.databinding.ViewMembershipCardBinding

class MembershipCardView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : ViewSwitcher(context, attributeSet) {

    private val binding = ViewMembershipCardBinding
        .inflate(LayoutInflater.from(context), this)

    init {
        binding.memberImage.outlineProvider = CircleOutlineProvider()
        binding.memberImage.clipToOutline = true

        setInAnimation(context, android.R.anim.fade_in)
        setOutAnimation(context, android.R.anim.fade_out)

        setOnLongClickListener { showNext(); true }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height: Int = (width * aspectRatio).toInt()

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
    }

    fun showFront() {
        if (nextView.id == R.id.card_front) {
            showNext()
        }
    }

    fun showBack() {
        if (nextView.id == R.id.card_back) {
            showNext()
        }
    }

    fun setMembership(membership: Membership) {
        with(membership) {
            setMembership(
                subscriptionType,
                membershipDisplayName,
                memberName,
                membershipId,
                shortCode,
                profileImageUrl,
                loyaltyId,
                isStaff
            )
        }
    }

    fun setMembership(
        subscriptionType: SubscriptionType,
        membershipDisplayName: Int?,
        memberName: String,
        membershipId: String,
        shortCode: String? = null,
        profileImageUrl: String? = null,
        loyaltyId: String? = null,
        isStaff: Boolean = false
    ) {
        setSubscriptionType(subscriptionType, membershipDisplayName, isStaff)
        with(binding) {
            this.memberName.text = memberName
            this.membershipId.text = shortCode ?: membershipId
            memberImage.setImageFromUrl(
                profileImageUrl,
                placeholder = R.drawable.ic_placeholder_profile,
                isRound = true
            )
            loyaltyId?.let { this.loyaltyId.text = it } ?: let { root.setVisible() }
        }
    }

    private fun setSubscriptionType(
        subscriptionType: SubscriptionType,
        membershipDisplayName: Int?,
        isStaff: Boolean = false
    ) {
        when {
            subscriptionType == SubscriptionType.FRIENDS -> {
                setupMembershipCard(
                    R.drawable.friends_card_bkg,
                    R.drawable.ic_membership_logo_soho_friends
                )
            }
            subscriptionType == SubscriptionType.CONNECT -> {
                setupMembershipCard(
                    R.drawable.background_membership_card_connect,
                    R.drawable.ic_membership_logo_soho_connect
                )
            }
            isStaff -> {
                setupMembershipCard(
                    R.drawable.background_membership_card_staff,
                    R.drawable.ic_membership_logo_soho_house
                )
            }
            else -> {
                setupMembershipCard(
                    R.drawable.background_membership_card_house,
                    R.drawable.ic_membership_logo_soho_house
                )
            }
        }

        membershipDisplayName?.let { binding.membershipType.setText(it) }
            ?: let { binding.membershipType.text = null }
    }

    private fun setupMembershipCard(@DrawableRes backgroundRes: Int, @DrawableRes logoRes: Int) {
        binding.houseLogo.setImageDrawable(ContextCompat.getDrawable(context, logoRes))
        background = ContextCompat.getDrawable(context, backgroundRes)
    }

    companion object {
        private const val aspectRatio = 0.64f
    }

    data class Membership(
        val subscriptionType: SubscriptionType,
        val membershipDisplayName: Int?,
        val memberName: String,
        val membershipId: String,
        val shortCode: String? = null,
        val profileImageUrl: String? = null,
        val loyaltyId: String? = null,
        val isStaff: Boolean = false
    )

}