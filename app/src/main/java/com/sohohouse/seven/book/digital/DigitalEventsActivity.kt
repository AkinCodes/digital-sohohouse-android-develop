package com.sohohouse.seven.book.digital

import android.content.Context
import android.content.Intent
import com.sohohouse.seven.R
import com.sohohouse.seven.base.InjectableActivity

class DigitalEventsActivity : InjectableActivity() {

    override fun getContentLayout(): Int = R.layout.activity_on_demand_events

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, DigitalEventsActivity::class.java)
        }
    }
}