package com.example.steptrackerwithlocaldatabase

import android.app.Notification
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
import androidx.annotation.MainThread
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
    private var stepOffset = STEP_OFFSET_MISSING

    companion object {
        private const val TAG = "StepCounterService"

        private const val CHANNEL_ID = "step_counter_service_channel"
        private const val CHANNEL_NAME = "Step Counter Service"
        private const val PENDING_INTENT_REQUEST_CODE = 0
        private const val NOTIFICATION_ID = 1
        private const val HISTORY_REPORT_INTERVAL_MILLIS = 15000L
        private const val STEP_OFFSET_MISSING = -1L
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        StepCounterServiceManager.serviceStarted = true
        startForeground(NOTIFICATION_ID, createNotification())
        tryStartHistoryWritingJob()
        tryRegisterStepSensorListener()
        return START_STICKY
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            PENDING_INTENT_REQUEST_CODE,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentIntent(pendingIntent).setSmallIcon(R.mipmap.ic_launcher).build()
    }

    private fun tryStartHistoryWritingJob() {
        if (job?.isActive != true) {
            job = serviceScope.launch {
                while (isActive) {
                    HistoryManager.appendToHistory(this@StepCounterService)
                    Log.d(TAG, "Saved step data")
                    delay(HISTORY_REPORT_INTERVAL_MILLIS)
                }
            }
        }
    }

    private fun tryRegisterStepSensorListener() {
        if (!sensorListenerRegistered) {
            val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensorListenerRegistered = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            } == true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        job?.cancel()
        job = null
        (getSystemService(Context.SENSOR_SERVICE) as SensorManager).unregisterListener(this)
        sensorListenerRegistered = false
        stepOffset = STEP_OFFSET_MISSING
        StepCounterServiceManager.serviceStarted = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            CoroutineScope(Dispatchers.Main).launch {
                StepCounterServiceManager.onSensorCallbackReceived()
            }
            val steps = event.values[0].toLong()
            Log.d(TAG, "Sensor event received, steps: $steps")
            if (stepOffset != STEP_OFFSET_MISSING) {
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

    @MainThread
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

    @MainThread
    fun stopStepCounter(context: Context) {
        val intent = Intent(context, StepCounterService::class.java)
        context.stopService(intent)
        currentlyCalibratingFlow.value = false
    }

    @MainThread
    fun onSensorCallbackReceived() {
        currentlyCalibratingFlow.value = false
    }
}