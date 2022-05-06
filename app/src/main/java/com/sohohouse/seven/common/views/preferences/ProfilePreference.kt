package com.sohohouse.seven.common.views.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.isVisible
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.sohohouse.seven.common.extensions.setImageFromUrl
import com.sohohouse.seven.common.extensions.setTextOrHide
import com.sohohouse.seven.common.extensions.stringProvider
import com.sohohouse.seven.common.views.CircleOutlineProvider
import com.sohohouse.seven.databinding.PreferenceAccountProfileBinding
import com.sohohouse.seven.more.AccountMenu

class ProfilePreference @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : Preference(context, attrs, defStyleAttr, defStyleRes) {

    private var profile: ProfileItem? = null
    private lateinit var customPreferenceButtonClick: (String) -> Unit

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)

        holder?.let {
            val binding = PreferenceAccountProfileBinding.bind(it.itemView)

            with(binding) {
                name.text = profile?.firstName
                staffIndication.isVisible = profile?.isStaff ?: false
                pronouns.setTextOrHide(
                    profile?.pronouns?.getPublicDisplayValue(context.stringProvider)
                )
                occupation.text = profile?.occupation

                image.apply {
                    outlineProvider = CircleOutlineProvider()
                    clipToOutline = true
                    setImageFromUrl(profile?.imageUrl, isRound = true)
                }

                buttonViewProfile.setOnClickListener {
                    bindClick(AccountMenu.LANDING_VIEW_PROFILE.key)
                }

                buttonShare.setOnClickListener {
                    bindClick(AccountMenu.LANDING_SHARE_PROFILE.key)
                }
            }
        }
    }

    private fun bindClick(key: String) {
        if (::customPreferenceButtonClick.isInitialized) customPreferenceButtonClick(key)
    }

    fun bind(profile: ProfileItem, customPreferenceButtonClick: (String) -> Unit) {
        this.profile = profile
        this.customPreferenceButtonClick = customPreferenceButtonClick
        notifyChanged()
    }

}