package com.example.p2.ui.main

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
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
import com.example.p2.R
import com.example.p2.ui.settings.SettingsActivity

@Composable
fun MainScreen() {
    val context = LocalContext.current
    var ipAddress by remember { mutableStateOf("http://192.168.4.1") }
    var webView: WebView? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = ipAddress,
            onValueChange = { ipAddress = it },
            label = { Text(stringResource(R.string.ip_address)) },
            modifier = Modifier.fillMaxWidth()
        )
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
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = {
                webView?.let { vw ->
                    val bitmap = Bitmap.createBitmap(vw.width, vw.height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    vw.draw(canvas)
                    Toast.makeText(context, context.getString(R.string.capture_done), Toast.LENGTH_SHORT).show()
                }
            }) {
                Text(stringResource(R.string.capture))
            }
            Button(onClick = {
                context.startActivity(Intent(context, SettingsActivity::class.java))
            }) {
                Text(stringResource(R.string.settings))
            }
        }
    }
}
