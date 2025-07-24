package com.example.p2.ui.settings

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.p2.R
import com.example.p2.ui.theme.P2Theme

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

@OptIn(ExperimentalMaterial3Api::class)
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
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = ip,
            onValueChange = {
                ip = it
                prefs.edit().putString("ip", it).apply()
            },
            label = { Text(stringResource(R.string.ip_address)) },
            modifier = Modifier.fillMaxWidth()
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
            valueRange = 10f..63f
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

