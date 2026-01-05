package com.example.ecorescueapp.ui.screens

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ecorescueapp.ui.navigation.Screen
import com.example.ecorescueapp.ui.viewmodel.LoginViewModel
import com.example.ecorescueapp.utils.BiometricAuth

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Estado para saber si el móvil soporta huella
    var canUseBiometric by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Comprobamos compatibilidad biométrica al iniciar la pantalla
    LaunchedEffect(Unit) {
        canUseBiometric = BiometricAuth.canAuthenticate(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "EcoRescue 🌱", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- BOTÓN LOGIN CLÁSICO ---
        Button(
            onClick = {
                viewModel.login(
                    email = email,
                    pass = password,
                    onLoginSuccess = { role ->
                        if (role == "ADMIN") {
                            navController.navigate(Screen.AdminHome.route)
                        } else {
                            navController.navigate(Screen.UserHome.route)
                        }
                    },
                    onError = {
                        Toast.makeText(context, "Error: Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- NUEVO BOTÓN BIOMÉTRICO (RA2.e) ---
        if (canUseBiometric) {
            OutlinedButton(
                onClick = {
                    // USAMOS LA FUNCIÓN DE ABAJO PARA ENCONTRAR LA ACTIVIDAD CORRECTAMENTE
                    val activity = context.findActivity()

                    if (activity != null) {
                        BiometricAuth.authenticate(
                            activity = activity,
                            onSuccess = {
                                Toast.makeText(context, "¡Huella verificada! 🔓", Toast.LENGTH_SHORT).show()
                                navController.navigate(Screen.AdminHome.route)
                            },
                            onError = {
                                Toast.makeText(context, "No se pudo verificar la huella", Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        Toast.makeText(context, "Error interno: Activity no encontrada", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Fingerprint, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Entrar con Huella")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- REGISTRO ---
        TextButton(
            onClick = { navController.navigate(Screen.Register.route) }
        ) {
            Text("¿No tienes cuenta? Regístrate aquí")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BOTÓN MÁGICO DE PRUEBAS
        OutlinedButton(
            onClick = {
                viewModel.seedDatabase()
                Toast.makeText(context, "Datos cargados (pan@eco.com / juan@eco.com)", Toast.LENGTH_LONG).show()
            }
        ) {
            Text("🛠️ Cargar Datos de Prueba")
        }
    }
}

// --- FUNCIÓN AUXILIAR IMPRESCINDIBLE ---
// Ayuda a encontrar la Actividad real aunque estemos dentro de temas o wrappers de Compose
fun Context.findActivity(): FragmentActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is FragmentActivity) return context
        context = context.baseContext
    }
    return null
}