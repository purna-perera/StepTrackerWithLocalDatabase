package com.example.steptrackerwithlocaldatabase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class StepCounterService : Service(), SensorEventListener {
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null
    private var sensorListenerRegistered = false
    private var stepOffset = -1L

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        StepCounterServiceManager.serviceStarted = true
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE  // <- Important for API 31+
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "step_counter_service_channel",
                "Step Counter Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat
            .Builder(this, "step_counter_service_channel")
            .setContentIntent(pendingIntent).setSmallIcon(R.mipmap.ic_launcher).build()
        startForeground(1, notification)
        if (job?.isActive != true) {
            job = serviceScope.launch {
                while (isActive) {
                    HistoryManager.appendToHistory(this@StepCounterService)
                    Log.d("StepCounterService", "Saved step data")
                    delay(15000)
                }
            }
        }
        if (!sensorListenerRegistered) {
            val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensorListenerRegistered = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            } == true
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        job?.cancel()
        job = null
        (getSystemService(Context.SENSOR_SERVICE) as SensorManager).unregisterListener(this)
        sensorListenerRegistered = false
        stepOffset = -1L
        StepCounterServiceManager.serviceStarted = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            StepCounterServiceManager.onSensorCallbackReceived()
            val steps = event.values[0].toLong()
            Log.d("StepCounterService", "Sensor event received, steps: $steps")
            if (stepOffset != -1L) {
                StepDataManager.incrementActualSteps(
                    this,
                    (steps - stepOffset).toInt()
                )
            }
            stepOffset = steps
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

object StepCounterServiceManager {
    val currentlyCalibratingFlow = MutableStateFlow(false)
    var serviceStarted = false

    fun startStepCounter(context: Context) {
        if (serviceStarted) {
            return
        }
        val intent = Intent(context, StepCounterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
        currentlyCalibratingFlow.value = true
    }

    fun stopStepCounter(context: Context) {
        val intent = Intent(context, StepCounterService::class.java)
        context.stopService(intent)
        currentlyCalibratingFlow.value = false
    }

    fun onSensorCallbackReceived() {
        currentlyCalibratingFlow.value = false
    }
}