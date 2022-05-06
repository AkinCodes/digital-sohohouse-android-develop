package com.sohohouse.seven.common.work

import android.app.NotificationManager
import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.sohohouse.seven.common.prefs.VenueWorker
import com.sohohouse.seven.common.venue.VenueRepo

class SohoWorkerFactory(
    private val venueRepo: VenueRepo,
    private val notificationManager: NotificationManager
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            VenueWorker::class.java.name -> VenueWorker(
                appContext,
                workerParameters,
                venueRepo,
                notificationManager
            )
            else -> null
        }
    }

}