package com.example.exercise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.exercise.ui.navigation.AppBottomNavigation
import com.example.exercise.ui.navigation.AppNavHost
import com.example.exercise.ui.screens.splash.SplashScreen
import com.example.exercise.ui.theme.ExerciseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExerciseTheme {
                AppContainer()
            }
        }
    }
}

@Composable
fun AppContainer() {
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        SplashScreen(
            onSplashFinished = { showSplash = false }
        )
    } else {
        MainScreen()
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AppBottomNavigation(navController)
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}