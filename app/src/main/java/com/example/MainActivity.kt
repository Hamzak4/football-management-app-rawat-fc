package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.ClubRepository
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ClubRepository.initialize(applicationContext)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        var isAuthorized by remember { mutableStateOf(ClubRepository.currentUser.value != null) }

        // Sync local auth state with the repository active session state
        LaunchedEffect(ClubRepository.currentUser.value) {
          isAuthorized = ClubRepository.currentUser.value != null
        }

        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          if (isAuthorized) {
            MainScreen(onLogout = { isAuthorized = false })
          } else {
            LoginScreen(onLoginSuccess = { isAuthorized = true })
          }
        }
      }
    }
  }
}

