package com.sohohouse.seven.shake

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager


class ShakeDetector(private val onShakeListener: ShakeListener) : SensorEventListener {

    companion object {
        // experimentally determined gForce threshold threshold to determine what counts as a shake
        private const val G_FORCE_THRESHOLD = 2.8f

        // used to continue a shake if there is a gap in g force values that are below threshold
        private const val SHAKE_TIME_DELTA_MS = 250L
    }

    private var isShaking = false
    private var lastShakeTimeMs = Long.MIN_VALUE

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // do nothing
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        val x = sensorEvent?.values?.get(0) ?: 0.0f
        val y = sensorEvent?.values?.get(1) ?: 0.0f
        val z = sensorEvent?.values?.get(2) ?: 0.0f

        val gX = x.div(SensorManager.GRAVITY_EARTH)
        val gY = y.div(SensorManager.GRAVITY_EARTH)
        val gZ = z.div(SensorManager.GRAVITY_EARTH)

        val gForce = calculateGravitationalForce(gX, gY, gZ)

        if (gForce > G_FORCE_THRESHOLD) {
            if (!isShaking) {
                onShakeListener.onShakeStarted()
                isShaking = true
            }
            lastShakeTimeMs = System.currentTimeMillis()
        } else {
            if (isShaking
                && (System.currentTimeMillis() - lastShakeTimeMs) > SHAKE_TIME_DELTA_MS
            ) {
                onShakeListener.onShakeStopped()
                isShaking = false
            }
        }
    }

    // value of g force while not moving on Earth is ~1.0f
    private fun calculateGravitationalForce(x: Float, y: Float, z: Float): Float {
        return Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
    }
}