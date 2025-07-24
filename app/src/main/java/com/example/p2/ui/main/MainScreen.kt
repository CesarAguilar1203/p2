package com.example.p2.ui.main

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.matchParentSize
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.p2.R
import com.example.p2.ui.main.MotionDetectionService
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
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = {
                        webView?.let { vw ->
                            val bitmap = Bitmap.createBitmap(vw.width, vw.height, Bitmap.Config.ARGB_8888)
                            val canvas = Canvas(bitmap)
                            vw.draw(canvas)
                            capturedBitmap = bitmap
                            Toast.makeText(context, context.getString(R.string.capture_done), Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.CameraAlt, contentDescription = stringResource(R.string.capture))
                    }
                    IconButton(onClick = {
                        context.startActivity(
                            Intent(context, SettingsActivity::class.java).putExtra("ip", ipAddress)
                        )
                    }) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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

            Box(
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
        }
    }
}

