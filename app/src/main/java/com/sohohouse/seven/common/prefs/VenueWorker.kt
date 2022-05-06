package com.sohohouse.seven.common.prefs

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.sohohouse.seven.R
import com.sohohouse.seven.common.venue.VenueRepo
import com.sohohouse.seven.network.auth.error.AuthError
import java.util.concurrent.TimeUnit

class VenueWorker(
    appContext: Context,
    workerParameters: WorkerParameters,
    private val venueRepo: VenueRepo,
    private val notificationManager: NotificationManager
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        setForegroundAsync()
        return venueRepo.fetchVenues().fold(
            ifEmptyOrError = {
                if (it == AuthError.INVALID_OAUTH_TOKEN)
                    Result.failure()
                else
                    Result.retry()
            },
            ifValue = { Result.success() }
        )
    }

    private suspend fun setForegroundAsync() {
        val title = applicationContext.getString(R.string.syncing)
        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_soho_house)
            .setContentTitle(title)
            .setNotificationSilent()
            .setContentText(applicationContext.getText(R.string.syncing_message))
            .setPriority(NotificationCompat.PRIORITY_LOW)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel().also {
                notificationBuilder.setChannelId(it.id)
            }
        }

        setForeground(ForegroundInfo(NOTIFICATION_ID, notificationBuilder.build()))
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): NotificationChannel {
        return NotificationChannel(
            CHANNEL_ID,
            applicationContext.getString(R.string.syncing),
            NotificationManager.IMPORTANCE_LOW
        ).also { channel -> notificationManager.createNotificationChannel(channel) }
    }

    companion object {

        private const val CHANNEL_ID = "1"
        private const val NOTIFICATION_ID = 1
        const val NAME = "VenueWorker"

        fun periodicRequest() = PeriodicWorkRequestBuilder<VenueWorker>(12, TimeUnit.HOURS)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()
    }
}