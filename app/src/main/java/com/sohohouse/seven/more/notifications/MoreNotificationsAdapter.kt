package com.sohohouse.seven.more.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.MoreNotificationsHeaderBinding
import com.sohohouse.seven.databinding.MoreNotificationsNotificationOptionBinding
import com.sohohouse.seven.databinding.MoreNotificationsPlatformSettingsBinding
import com.sohohouse.seven.more.notifications.recyclerview.*
import com.sohohouse.seven.more.notifications.recyclerview.MoreNotificationsAdapterItemType.*
import com.sohohouse.seven.more.notifications.recyclerview.MoreNotificationsTopSupportingViewHolder.Companion.MORE_NOTIFICATIONS_TOP_SUPPORTING

interface NotificationEventsToggleListener {
    fun toggle(isPushToogle: Boolean)
}

interface MoreNotificationsSwitchListener {
    fun onOptionSwitched(
        key: String,
        currentValue: Boolean,
        isPushNotification: Boolean = false,
        defaultState: Boolean = true,
        listener: NotificationEventsToggleListener
    )
}

interface MoreNotificationsButtonListener {
    fun onAndroidNotificationsSettingsClicked()
}

class MoreNotificationsAdapter(
    val dataItems: List<MoreNotificationsAdapterItem>,
    private val switchListener: MoreNotificationsSwitchListener,
    private val buttonListener: MoreNotificationsButtonListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (values()[viewType]) {
            TOP_SUPPORTING -> MoreNotificationsTopSupportingViewHolder(
                inflater.inflate(MORE_NOTIFICATIONS_TOP_SUPPORTING, parent, false)
            )
            HEADER -> MoreNotificationsHeaderViewHolder(
                MoreNotificationsHeaderBinding.inflate(inflater, parent, false)
            )
            NOTIFICATION_OPTION -> MoreNotificationsNotificationOptionViewHolder(
                MoreNotificationsNotificationOptionBinding.inflate(inflater, parent, false)
            )
            PLATFORM_SETTINGS -> MoreNotificationsPlatformSettingsViewHolder(
                MoreNotificationsPlatformSettingsBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataItems[position]
        when (values()[holder.itemViewType]) {
            HEADER -> (holder as MoreNotificationsHeaderViewHolder).bind(item as MoreNotificationsHeaderAdapterItem)
            NOTIFICATION_OPTION -> (holder as MoreNotificationsNotificationOptionViewHolder)
                .bind(item as MoreNotificationsNotificationOptionAdapterItem, switchListener)
            PLATFORM_SETTINGS -> (holder as MoreNotificationsPlatformSettingsViewHolder).bind(
                buttonListener
            )
//            CONTACT_HOUSE -> (holder as MoreNotificationsContactHouseViewHolder).bind(buttonListener)
            else -> {
            }
        }
    }

    override fun getItemCount(): Int {
        return dataItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return dataItems[position].itemType.ordinal
    }
}