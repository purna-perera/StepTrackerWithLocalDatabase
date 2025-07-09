package com.example.steptrackerwithlocaldatabase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class DataWriterService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE  // <- Important for API 31+
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "data_writer_service_channel",
                "Data Writer Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat
            .Builder(this, "data_writer_service_channel")
            .setContentIntent(pendingIntent).setSmallIcon(R.mipmap.ic_launcher).build()
        startForeground(1, notification)
        if (job?.isActive != true) {
            job = serviceScope.launch {
                while (isActive) {
                    Log.d("DataWriterService", "Saved step data")
                    delay(60000)
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        job?.cancel()
        job = null
    }
}

object DataWriterController {
    fun startDataWriter(context: Context) {
        val dataWriterIntent = Intent(context, DataWriterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(dataWriterIntent)
        } else {
            context.startService(dataWriterIntent)
        }
    }
}