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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ecorescueapp.ui.navigation.Screen
import com.example.ecorescueapp.ui.theme.VerdePrincipal
import com.example.ecorescueapp.ui.viewmodel.LoginViewModel
import com.example.ecorescueapp.utils.BiometricAuth

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var canUseBiometric by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) { canUseBiometric = BiometricAuth.canAuthenticate(context) }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF0D0D0D)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "FoodShare 🌱", style = MaterialTheme.typography.displayMedium, color = VerdePrincipal, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email") }, modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VerdePrincipal)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VerdePrincipal)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.login(email, password,
                        onLoginSuccess = { role -> navController.navigate(if (role == "ADMIN") Screen.AdminHome.route else Screen.UserHome.route) },
                        onError = { Toast.makeText(context, "Error credenciales", Toast.LENGTH_SHORT).show() }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
            ) { Text("INICIAR SESIÓN", color = Color.Black) }

            Spacer(modifier = Modifier.height(16.dp))

            if (canUseBiometric) {
                OutlinedButton(
                    onClick = {
                        val activity = context.findActivity()
                        if (activity != null) {
                            BiometricAuth.authenticate(activity,
                                onSuccess = {
                                    Toast.makeText(context, "Acceso Huella Correcto", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Screen.AdminHome.route)
                                },
                                onError = { Toast.makeText(context, "Error Huella", Toast.LENGTH_SHORT).show() }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VerdePrincipal),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VerdePrincipal)
                ) {
                    Icon(Icons.Default.Fingerprint, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ENTRAR CON HUELLA")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { navController.navigate(Screen.Register.route) }) { Text("¿No tienes cuenta? Regístrate aquí", color = Color.Gray) }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { viewModel.seedDatabase(); Toast.makeText(context, "Datos cargados", Toast.LENGTH_SHORT).show() }, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) { Text("🛠️ CARGAR DATOS DE PRUEBA") }
        }
    }
}

fun Context.findActivity(): FragmentActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is FragmentActivity) return context
        context = context.baseContext
    }
    return null
}