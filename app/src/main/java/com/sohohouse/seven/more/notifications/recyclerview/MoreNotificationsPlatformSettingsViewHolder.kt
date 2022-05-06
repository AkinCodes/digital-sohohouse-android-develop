package com.sohohouse.seven.more.notifications.recyclerview

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.databinding.MoreNotificationsPlatformSettingsBinding
import com.sohohouse.seven.more.notifications.MoreNotificationsButtonListener


class MoreNotificationsPlatformSettingsViewHolder(private val binding: MoreNotificationsPlatformSettingsBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(listener: MoreNotificationsButtonListener) {
        binding.settingsButton.clicks {
            listener.onAndroidNotificationsSettingsClicked()
        }
    }
}