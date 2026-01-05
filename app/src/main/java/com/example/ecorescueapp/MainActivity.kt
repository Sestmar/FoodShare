package com.example.ecorescueapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity // <--- CAMBIO IMPORTANTE
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ecorescueapp.ui.navigation.Screen
import com.example.ecorescueapp.ui.screens.AdminHomeScreen
import com.example.ecorescueapp.ui.screens.LoginScreen
import com.example.ecorescueapp.ui.screens.RegisterScreen
import com.example.ecorescueapp.ui.screens.ReportScreen
import com.example.ecorescueapp.ui.screens.UserHomeScreen
import com.example.ecorescueapp.ui.theme.EcoRescueAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() { // <--- HEREDA DE AppCompatActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EcoRescueAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.AdminHome.route) { AdminHomeScreen(navController) }
        composable(Screen.UserHome.route) { UserHomeScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.Report.route) { ReportScreen(navController) }
    }
}