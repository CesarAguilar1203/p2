package com.example.p2.ui.main

import android.content.Intent
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.runtime.DisposableEffect
import android.os.Build
import android.graphics.Bitmap
import android.graphics.Canvas
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.Image
import com.example.p2.ui.main.MotionDetectionService
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.graphics.asImageBitmap
import com.example.p2.R
import com.example.p2.ui.settings.SettingsActivity

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }
    var ipAddress by remember { mutableStateOf(prefs.getString("ip", "http://192.168.4.1") ?: "http://192.168.4.1") }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                ipAddress = prefs.getString("ip", ipAddress) ?: ipAddress
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    var webView: WebView? by remember { mutableStateOf(null) }
    var monitoring by remember { mutableStateOf(false) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = ipAddress,
            onValueChange = {
                ipAddress = it
                prefs.edit().putString("ip", it).apply()
            },
            label = { Text(stringResource(R.string.ip_address)) },
            modifier = Modifier.fillMaxWidth()
        )
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        webView = this
                    }
                },
                update = { view ->
                    view.loadUrl(ipAddress)
                },
                modifier = Modifier.matchParentSize()
            )
            capturedBitmap?.let { bmp ->
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = stringResource(R.string.capture_done),
                    modifier = Modifier.matchParentSize()
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = {
                webView?.let { vw ->
                    val bitmap = Bitmap.createBitmap(vw.width, vw.height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    vw.draw(canvas)
                    capturedBitmap = bitmap
                    Toast.makeText(context, context.getString(R.string.capture_done), Toast.LENGTH_SHORT).show()
                }
            }) {
                Text(stringResource(R.string.capture))
            }
            Button(onClick = {
                context.startActivity(
                    Intent(context, SettingsActivity::class.java).putExtra("ip", ipAddress)
                )
            }) {
                Text(stringResource(R.string.settings))
            }
            Button(onClick = {
                monitoring = !monitoring
                val intent = Intent(context, MotionDetectionService::class.java).putExtra("ip", ipAddress)
                if (monitoring) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent)
                    } else {
                        context.startService(intent)
                    }
                } else {
                    context.stopService(intent)
                }
            }) {
                Text(
                    if (monitoring) stringResource(R.string.stop_monitoring)
                    else stringResource(R.string.start_monitoring)
                )
            }
        }
    }
}
