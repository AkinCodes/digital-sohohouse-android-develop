package com.sohohouse.seven.home.houseboard.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.sohohouse.seven.R
import com.sohohouse.seven.common.extensions.getString
import com.sohohouse.seven.common.extensions.replaceBraces
import com.sohohouse.seven.databinding.ViewHolderNotificationBinding
import com.sohohouse.seven.home.houseboard.items.NotificationItem
import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime

class NotificationViewHolder(private val binding: ViewHolderNotificationBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: NotificationItem) = with(binding) {
        title.text = item.title
        subtitle.text = item.body
        time.text = getAgo(item.createdAt)
        image.setImageUrl(item.imageUrl)
    }

    private fun getAgo(dateTime: ZonedDateTime?): String? {
        if (dateTime == null) return null

        val duration = Duration.between(dateTime, ZonedDateTime.now())
        val weeks = duration.toDays() / 7
        val days = duration.toDays()
        val hours = duration.toHours()
        val minutes = duration.toMinutes()
        return when {
            weeks > 1 -> getString(R.string.notification_center_weeks_ago_abbr).replaceBraces("$weeks")
            days > 0 -> getString(R.string.notification_center_days_ago_abbr).replaceBraces("$days")
            hours > 0 -> getString(R.string.notification_center_hours_ago_abbr).replaceBraces("$hours")
            minutes > 0 -> getString(R.string.notification_center_minutes_ago_abbr).replaceBraces("$minutes")
            else -> getString(R.string.notification_center_now)
        }
    }
}
