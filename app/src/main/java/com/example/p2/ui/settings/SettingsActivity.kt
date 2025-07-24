package com.example.p2.ui.settings


import android.content.Context

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.p2.R
import com.example.p2.ui.theme.P2Theme
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ipAddress = intent.getStringExtra("ip") ?: ""



        enableEdgeToEdge()
        setContent {
            P2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    SettingsScreen(ipAddress = ipAddress)
                }
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(ipAddress: String) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }
    var ip by remember { mutableStateOf(ipAddress) }

    var expanded by remember { mutableStateOf(false) }
    val resolutions = listOf(
        "QVGA" to 4,
        "VGA" to 6,
        "SVGA" to 7,
        "XGA" to 8
    )
    var selectedRes by remember { mutableStateOf(resolutions[2]) }
    var quality by remember { mutableStateOf(10f) }
    var brightness by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = ip,
            onValueChange = {
                ip = it
                prefs.edit().putString("ip", it).apply()
            },
            label = { Text(stringResource(R.string.ip_address)) },
            modifier = Modifier.fillMaxWidth(),
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedRes.first,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.resolution)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                resolutions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.first) },
                        onClick = {
                            selectedRes = option
                            expanded = false
                        }
                    )
                }
            }
        }

        Text(stringResource(R.string.quality))
        Slider(
            value = quality,
            onValueChange = { quality = it },
            valueRange = 10f..63f,
            steps = 0
        )

        Text(stringResource(R.string.brightness))
        Slider(
            value = brightness,
            onValueChange = { brightness = it },
            valueRange = -2f..2f,
            steps = 4
        )

        Button(
            onClick = {
                prefs.edit().putString("ip", ip).apply()
                sendSetting(ip, "framesize", selectedRes.second)
                sendSetting(ip, "quality", quality.toInt())
                sendSetting(ip, "brightness", brightness.toInt())

                Toast.makeText(context, context.getString(R.string.settings_sent), Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.apply))
        }
    }
}


private fun sendSetting(ip: String, variable: String, value: Int) {
    if (ip.isBlank()) return
    thread {
        try {
            val url = URL("$ip/control?var=$variable&val=$value")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 2000
            conn.connect()
            conn.inputStream.close()
            conn.disconnect()
        } catch (_: Exception) {
        }
    }
}
