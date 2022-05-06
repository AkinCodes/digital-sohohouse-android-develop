package com.sohohouse.seven.profile.edit.socialmedia

import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sohohouse.seven.R
import com.sohohouse.seven.base.DiffItem
import com.sohohouse.seven.base.mvvm.BaseViewModel
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.utils.StringProvider
import com.sohohouse.seven.profile.ProfileField
import com.sohohouse.seven.profile.SocialMediaItem
import com.sohohouse.seven.profile.SocialMediaItem.Type.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class EditSocialMediaViewModel @AssistedInject constructor(
    private val stringProvider: StringProvider,
    @Assisted val field: ProfileField.SocialMedia,
    analyticsManager: AnalyticsManager,
) : BaseViewModel(analyticsManager) {

    private val _items = MutableLiveData<List<DiffItem>>()
    val items: LiveData<List<DiffItem>> get() = _items

    init {
        populateList()
    }

    private fun populateList() {
        if (_items.value != null) return
        _items.value = mutableListOf<DiffItem>().apply {
            addAll(field.data.map { socialMediaItem ->
                SocialMediaAdapterItem(
                    label = socialMediaItem.name,
                    hint = socialMediaItem.hint,
                    _value = socialMediaItem.value,
                    errors = socialMediaItem.errors,
                    onEdited = { input -> socialMediaItem.onEdited(input) },
                    enabled = field.optIn
                )
            })
            add(
                SwitchWithLabelItem(switchedOn = field.optIn,
                    label = stringProvider.getString(R.string.social_media_visibility_switch_label),
                    onToggleSwitch = { optInChecked ->
                        onOptInChecked(field, optInChecked)
                        sendAnalytics(optInChecked)
                    })
            )
        }
    }

    private fun sendAnalytics(optInChecked: Boolean) {
        analyticsManager.logEventAction(
            AnalyticsManager.Action.ShareSocialConnectionsToggle, bundleOf(
                AnalyticsManager.Parameters.ShareSocialConnectionsToggleEnabledState.value to optInChecked
            )
        )
    }

    private fun onOptInChecked(field: ProfileField.SocialMedia, optInChecked: Boolean) {
        field.optIn = optInChecked

        _items.value = _items.value?.toMutableList()?.apply {
            forEachIndexed { index, diffItem ->
                (diffItem as? SocialMediaAdapterItem?)?.let {
                    set(index, it.copy(enabled = optInChecked))
                }
            }
        }
    }

    private fun SocialMediaItem.onEdited(input: String) {
        when (type) {
            SPOTIFY -> url = input
            YOUTUBE -> url = input
            LINKEDIN -> url = input
            TWITTER -> handle = input
            INSTAGRAM -> handle = input
            WEBSITE -> url = input
        }
    }

    private val SocialMediaItem.value: String?
        get() = when (type) {
            SPOTIFY -> url
            YOUTUBE -> url
            LINKEDIN -> url
            TWITTER -> handle
            INSTAGRAM -> handle
            WEBSITE -> url
        }

    private val SocialMediaItem.hint: String
        get() = when (type) {
            SPOTIFY -> stringProvider.getString(R.string.profile_spotify_placeholder)
            YOUTUBE -> stringProvider.getString(R.string.profile_youtube_placeholder)
            LINKEDIN -> stringProvider.getString(R.string.profile_linkedin_placeholder)
            TWITTER -> stringProvider.getString(R.string.profile_twitter_placeholder)
            INSTAGRAM -> stringProvider.getString(R.string.profile_instagram_placeholder)
            WEBSITE -> stringProvider.getString(R.string.profile_website_placeholder)
        }

    @AssistedFactory
    interface Factory {
        fun create(field: ProfileField.SocialMedia): EditSocialMediaViewModel
    }

}
