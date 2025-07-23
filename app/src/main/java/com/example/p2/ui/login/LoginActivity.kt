package com.example.p2.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.p2.MainActivity
import com.example.p2.ui.theme.P2Theme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            P2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    LoginScreen(onLoginSuccess = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    })
                }
            }
        }
    }
}
