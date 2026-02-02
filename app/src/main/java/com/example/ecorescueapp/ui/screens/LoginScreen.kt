package com.example.ecorescueapp.ui.screens

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ecorescueapp.ui.component.FloatingFoodBackground
import com.example.ecorescueapp.ui.components.FloatingParticles
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
    ) {
        // 1. FONDO DE PARTÍCULAS (Luces flotantes)
        FloatingParticles(
            particleCount = 60,
            color = VerdePrincipal
        )

        // 2. FONDO DE COMIDA (Figuras)
        FloatingFoodBackground()

        // 3. CONTENIDO PRINCIPAL
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // LOGO
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    // Sombra neón correcta
                    .shadow(elevation = 20.dp, spotColor = VerdePrincipal, shape = CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // TEXTOS
            Text(
                text = "FoodShare",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            Text(
                text = "Rescata. Comparte. Disfruta.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ESTILOS DE INPUT
            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VerdePrincipal,
                unfocusedBorderColor = Color(0xFF333333),
                focusedLabelColor = VerdePrincipal,
                unfocusedLabelColor = Color.Gray,
                cursorColor = VerdePrincipal,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color(0xFF1E1E1E),
                unfocusedContainerColor = Color(0xFF1E1E1E),
                focusedLeadingIconColor = VerdePrincipal,
                unfocusedLeadingIconColor = Color.Gray
            )

            // EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Corporativo") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // PASSWORD
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // BOTÓN LOGIN (Aquí estaba el error, ya corregido)
            Button(
                onClick = {
                    viewModel.login(email, password,
                        onLoginSuccess = { role ->
                            navController.navigate(if (role == "ADMIN") Screen.AdminHome.route else Screen.UserHome.route)
                        },
                        onError = {
                            Toast.makeText(context, "Error credenciales", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    // CORRECCIÓN APLICADA: spotColor para el color, shape para la forma
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(12.dp),
                        spotColor = VerdePrincipal,
                        ambientColor = VerdePrincipal
                    ),
                colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("INICIAR SESIÓN", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BIOMETRÍA
            if (canUseBiometric) {
                OutlinedButton(
                    onClick = {
                        val activity = context.findActivity()
                        activity?.let {
                            BiometricAuth.authenticate(it,
                                onSuccess = {
                                    Toast.makeText(context, "Acceso Concedido", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Screen.AdminHome.route)
                                },
                                onError = { Toast.makeText(context, "No reconocido", Toast.LENGTH_SHORT).show() }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VerdePrincipal),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VerdePrincipal.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Fingerprint, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ENTRAR CON HUELLA")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                Text(
                    "Crear cuenta nueva",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón oculto para cargar datos demo
            Box(modifier = Modifier
                .clickable { viewModel.seedDatabase(); Toast.makeText(context, "Datos cargados", Toast.LENGTH_SHORT).show() }
                .size(50.dp)
            )
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