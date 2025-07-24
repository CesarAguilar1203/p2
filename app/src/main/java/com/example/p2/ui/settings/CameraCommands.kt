package com.example.p2.ui.settings

import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

fun sendSetting(ip: String, variable: String, value: Int) {
    if (ip.isBlank()) return
    val targetIp =
        if (ip.startsWith("http://") || ip.startsWith("https://")) ip else "http://$ip"
    thread {
        try {
            val url = URL("$targetIp/control?var=$variable&val=$value")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 2000
            connection.inputStream.close()
            connection.disconnect()
        } catch (_: Exception) {
        }
    }
}
