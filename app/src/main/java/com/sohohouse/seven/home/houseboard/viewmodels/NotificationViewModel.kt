package com.sohohouse.seven.home.houseboard.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sohohouse.seven.base.mvvm.IViewModel
import com.sohohouse.seven.base.mvvm.IViewModelBaseImpl
import com.sohohouse.seven.home.houseboard.items.NotificationItem
import com.sohohouse.seven.home.houseboard.repo.NotificationsRepo
import com.sohohouse.seven.network.core.models.notification.Notification
import com.sohohouse.seven.network.core.models.notification.NotificationGroup
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.io.IOException
import java.util.concurrent.CancellationException
import java.util.concurrent.CopyOnWriteArrayList

interface NotificationViewModel : IViewModel {

    val notifications: LiveData<List<NotificationItem>>

    val notificationBadge: LiveData<Boolean>

    fun startPolling(initialDelay: Long = 0)

    fun stopPolling()

    fun getNotifications()

    fun patchNotification(
        notification: NotificationItem,
        seen: Boolean = notification.seen,
        dismissed: Boolean = notification.dismissed,
        complete: Boolean = notification.complete
    )

    fun patchNotificationGroup(
        notificationGroup: NotificationGroup,
        seen: Boolean = notificationGroup.seen,
        dismissed: Boolean = notificationGroup.dismissed,
        new: Boolean = notificationGroup.new
    )

    fun dismissNotificationBadge()

    fun dismissAllNotifications()

    fun clearNotificationJobs()

    fun markFirstNotifAsRead()
}

open class NotificationViewModelImpl(private val repo: NotificationsRepo) : IViewModelBaseImpl(),
    NotificationViewModel {

    private val pollingJob = CopyOnWriteArrayList<Job>()

    private val _notifications = MutableLiveData<List<NotificationItem>>()

    override val notifications: LiveData<List<NotificationItem>>
        get() = _notifications

    private var notificationGroup: NotificationGroup? = null

    private val _notificationBadge = MutableLiveData<Boolean>()

    override val notificationBadge: LiveData<Boolean>
        get() = _notificationBadge

    private fun onNewNotifications(notifications: List<Notification>) {
        val items = notifications.map { NotificationItem(it) }
            .filter { it.navigationTrigger?.notYetSupported == false }
        _notifications.postValue(items)
        if (items.isEmpty()) return

        setHeaderBadge(hasNewNotification(items))
        notificationGroup = items.firstOrNull()?.notificationGroup
    }

    private fun hasNewNotification(items: List<NotificationItem>): Boolean {
        return items.count { item -> !item.seen } > 0
    }

    private fun onError(throwable: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }

    override fun clearNotificationJobs() {
        stopPolling()
        supervisorJob.cancel()
    }

    override fun markFirstNotifAsRead() {
        _notifications.value?.firstOrNull()?.let {
            patchNotification(it, seen = true)
        }
    }

    override fun startPolling(initialDelay: Long) {
        viewModelScope.launch {
            try {
                delay(initialDelay)
                poll { getNotifications() }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }.also { job -> track(job) }
    }

    override fun stopPolling() {
        pollingJob.forEach { it.cancel() }
        pollingJob.clear()
    }

    override fun getNotifications() {
        viewModelScope.launch {
            repo.getNotifications().fold(
                ifError = { onError(Throwable(it.toString())) },
                ifValue = { onNewNotifications(it) },
                ifEmpty = { onNewNotifications(emptyList()) })
        }
    }

    override fun patchNotification(
        notification: NotificationItem,
        seen: Boolean,
        dismissed: Boolean,
        complete: Boolean
    ) {
        if (dismissed) {
            val list = _notifications.value?.toMutableList() ?: mutableListOf()
            list.remove(notification)
            _notifications.postValue(list)
        }

        if (notification.id == null) return

        viewModelScope.launch {
            repo.patch(Notification(notification.id, seen, dismissed, complete)).fold(
                ifError = { onError(Throwable(it.toString())) },
                ifValue = { onNotificationPatched(NotificationItem(it)) },
                ifEmpty = {}
            )
        }
    }

    private fun onNotificationPatched(item: NotificationItem) {
        if (item.dismissed) return
        val notifications = _notifications.value?.toMutableList() ?: return
        val oldItem = notifications.firstOrNull { it.id == item.id } ?: return
        val position = notifications.indexOf(oldItem)
        val newItem = oldItem.copy(
            seen = item.seen,
            complete = item.complete,
            persistent = item.persistent,
            new = item.new
        )
        notifications[position] = newItem
        setHeaderBadge(hasNewNotification(notifications))
    }

    override fun patchNotificationGroup(
        notificationGroup: NotificationGroup,
        seen: Boolean,
        dismissed: Boolean,
        new: Boolean
    ) {
        if (notificationGroup.id == null) return

        viewModelScope.launch {
            repo.patch(NotificationGroup(notificationGroup.id, seen, dismissed, new)).fold(
                ifError = {},
                ifValue = { onNotificationGroupPatched(it) },
                ifEmpty = {}
            )
        }
    }

    private fun onNotificationGroupPatched(group: NotificationGroup) {
        notificationGroup = group
        setHeaderBadge(notificationGroup?.seen == true)
        getNotifications()
    }

    override fun dismissNotificationBadge() {
        notificationGroup?.takeIf { !it.seen }
            ?.let { notificationGroup -> patchNotificationGroup(notificationGroup, seen = true) }
    }

    override fun dismissAllNotifications() {
        _notifications.postValue(emptyList())
        notificationGroup?.run { patchNotificationGroup(this, dismissed = true) }
    }

    private fun setHeaderBadge(value: Boolean) {
        _notificationBadge.postValue(value)
    }

    private suspend fun poll(
        initialDelay: Long = NOTIFICATION_FETCH_INTERVAL,
        maxDelay: Long = NOTIFICATION_FETCH_INTERVAL,
        factor: Double = 2.0,
        block: suspend () -> Unit
    ) {
        var currentDelay = initialDelay
        while (true) {
            try {
                currentDelay = try {
                    block()
                    initialDelay
                } catch (e: IOException) {
                    (currentDelay * factor).toLong().coerceAtMost(maxDelay)
                }
                delay(currentDelay)
                yield()
            } catch (e: CancellationException) {
                break
            }
        }
    }

    private fun track(job: Job) {
        pollingJob.add(job)
        job.invokeOnCompletion {
            pollingJob.remove(job)
        }
    }

    companion object {
        private const val NOTIFICATION_FETCH_INTERVAL = 30000L     // 30 sec
    }

}