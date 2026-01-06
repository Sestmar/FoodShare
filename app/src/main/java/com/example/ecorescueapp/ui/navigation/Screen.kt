package com.example.ecorescueapp.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object AdminHome : Screen("admin_home") // Pantalla del Comercio
    object UserHome : Screen("user_home")   // Pantalla del Voluntario
    object Report : Screen("report") // pantalla de reportes por PDF
    object UserHistory : Screen("user_history_screen")
}