package com.example.ecorescueapp.utils

/**
 * Singleton para gestionar la sesión del usuario actual en memoria.
 * Esto permite que 'UserViewModel' sepa quién está reservando realmente.
 */
object CurrentUser {
    var activeUser: String = "Usuario Anónimo"
    var activeRole: String = ""
    var activeEmail: String = ""
}