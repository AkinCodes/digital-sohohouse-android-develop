package com.sohohouse.seven.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import java.util.*

class DateChangeReceiver(private val listener: (Int) -> Unit) : BroadcastReceiver() {

    init {
        notifyDateChanged()
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        notifyDateChanged()
    }

    private fun notifyDateChanged() {
        listener(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
    }

    fun register(context: Context) {
        context.registerReceiver(this, IntentFilter().apply {
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_DATE_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
        })
        notifyDateChanged()
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(this)
    }

}