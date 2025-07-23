package com.example.p2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.p2.ui.login.LoginScreen
import com.example.p2.ui.theme.P2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            P2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    LoginScreen()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    P2Theme {
        LoginScreen()
    }
}