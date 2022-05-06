package com.sohohouse.seven.more.notifications.recyclerview

import android.view.View
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.databinding.MoreNotificationsNotificationOptionBinding
import com.sohohouse.seven.more.notifications.MoreNotificationsSwitchListener
import com.sohohouse.seven.more.notifications.NotificationEventsToggleListener

class MoreNotificationsNotificationOptionViewHolder(private val binding: MoreNotificationsNotificationOptionBinding) :
    RecyclerView.ViewHolder(binding.root),
    NotificationEventsToggleListener {

    private lateinit var item: MoreNotificationsNotificationOptionAdapterItem

    override fun toggle(isPushToogle: Boolean) = with(binding) {
        if (isPushToogle) {
            pushSwitch.toggle()
            item.startingValue = pushSwitch.isChecked
        } else {
            emailSwitch.toggle()
            item.emailState = emailSwitch.isChecked
        }
    }

    fun bind(
        item: MoreNotificationsNotificationOptionAdapterItem,
        listener: MoreNotificationsSwitchListener
    ) = with(binding) {
        this@MoreNotificationsNotificationOptionViewHolder.item = item

        subHeader.text = item.subHeaderText
        supporting.text = item.supportingText

        emailSwitch.isVisible = item.hasEmailNotifications

        pushSwitch.isChecked = item.startingValue
        pushSwitch.setOnSwitchClickListenerWithoutUpdatingItsState {
            listener.onOptionSwitched(
                item.key,
                pushSwitch.isChecked,
                true,
                item.defaultState,
                this@MoreNotificationsNotificationOptionViewHolder
            )
        }

        if (item.hasEmailNotifications) {
            emailSwitch.isChecked = item.emailState

            emailSwitch.setOnSwitchClickListenerWithoutUpdatingItsState {
                listener.onOptionSwitched(
                    item.emailKey,
                    emailSwitch.isChecked,
                    false,
                    item.emailState,
                    this@MoreNotificationsNotificationOptionViewHolder
                )
            }
        }
    }

    private fun SwitchCompat.setOnSwitchClickListenerWithoutUpdatingItsState(onClick: (View) -> Unit) {
        setOnClickListener {
            toggle()
            onClick(it)
        }
    }

}