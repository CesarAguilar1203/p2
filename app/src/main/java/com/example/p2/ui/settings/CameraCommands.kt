package com.example.p2.ui.settings

import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

fun sendSetting(ip: String, variable: String, value: Int) {
    if (ip.isBlank()) return
    thread {
        try {
            val url = URL("$ip/control?var=$variable&val=$value")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 2000
            connection.inputStream.close()
            connection.disconnect()
        } catch (_: Exception) {
        }
    }
}
