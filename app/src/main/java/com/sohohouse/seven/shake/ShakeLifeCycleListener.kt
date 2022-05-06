package com.sohohouse.seven.shake

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import com.sohohouse.seven.base.mvpimplementation.ActivityLifeCycleListener

class ShakeLifeCycleListener(activity: Activity?) : ActivityLifeCycleListener {

    private var activity: Activity? = null
    private var shakeDetector: ShakeDetector? = null

    init {
        if (isActivityListeningForShakes(activity)) {
            this.activity = activity
            this.shakeDetector = ShakeDetector(activity as ShakeListener)
        }
    }

    override fun onResume() {
        if (isActivityListeningForShakes(activity)) {
            val sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensorManager.registerListener(
                shakeDetector,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onPause() {
        if (isActivityListeningForShakes(activity)) {
            val sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensorManager.unregisterListener(shakeDetector)
        }
    }

    override fun onPostCreated(activity: Activity?, savedInstanceState: Bundle?) {
        // do nothing
    }


    override fun onDestroy() {
        // do nothing
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // do nothing
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        // do nothing
    }

    private fun isActivityListeningForShakes(activity: Activity?): Boolean {
        return activity != null
                && activity is ShakeListener
    }

}