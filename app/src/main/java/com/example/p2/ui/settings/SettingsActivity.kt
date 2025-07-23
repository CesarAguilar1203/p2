package com.example.p2.ui.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.p2.ui.theme.P2Theme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            P2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Text(text = "Configuraciones")
                    }
                }
            }
        }
    }
}
