package com.example.p2.ui.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.p2.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

class MotionDetectionService : Service() {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var ip: String = ""

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ip = intent?.getStringExtra("ip") ?: return START_NOT_STICKY
        if (ip.isBlank()) return START_NOT_STICKY
        createChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.monitoring))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        startForeground(1, notification)
        scope.launch {
            while (isActive) {
                try {
                    val url = URL("$ip/motion")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.connectTimeout = 2000
                    conn.readTimeout = 2000
                    val response = conn.inputStream.bufferedReader().readText()
                    conn.disconnect()
                    if (response.contains("1")) {
                        sendIntruderNotification()
                    }
                } catch (_: Exception) {
                }
                delay(5000)
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun sendIntruderNotification() {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.intruder_detected))
            .setContentText(getString(R.string.intruder_detected))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        nm.notify(2, notification)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Motion",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "motion_channel"
    }
}
