package com.example.ecorescueapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ecorescueapp.ui.component.FloatingFoodBackground
import com.example.ecorescueapp.ui.component.NeonBorderBox
import com.example.ecorescueapp.ui.theme.VerdePrincipal
import com.example.ecorescueapp.ui.viewmodel.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    // RA1.d: Personalización de componentes y gestión de estado
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("USER") } // Por defecto Voluntario
    val context = LocalContext.current

    // Estilo unificado para los campos de texto (Cyberpunk)
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = VerdePrincipal,
        unfocusedBorderColor = Color.DarkGray,
        focusedLabelColor = VerdePrincipal,
        cursorColor = VerdePrincipal,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedContainerColor = Color(0xFF1E1E1E),
        unfocusedContainerColor = Color(0xFF1E1E1E)
    )

    Scaffold(
        containerColor = Color(0xFF0D0D0D),
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        // RA4.g: Uso de Box para capas (Fondo animado + Contenido)
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            // --- CAPA 1: FONDO ANIMADO ---
            FloatingFoodBackground()

            // --- CAPA 2: FORMULARIO ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Únete a FoodShare",
                    style = MaterialTheme.typography.headlineMedium,
                    color = VerdePrincipal,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Crea tu cuenta para empezar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Inputs de texto estilizados
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Nombre Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email, onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password, onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Selector de Rol (Admin/User)
                Text("Selecciona tu perfil:", color = Color.Gray, modifier = Modifier.align(Alignment.Start))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("USER" to "Voluntario", "ADMIN" to "Comercio").forEach { (role, label) ->
                        FilterChip(
                            selected = selectedRole == role,
                            onClick = { selectedRole = role },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VerdePrincipal,
                                selectedLabelColor = Color.Black,
                                containerColor = Color(0xFF1E1E1E),
                                labelColor = Color.Gray
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // RA4.g: Botón principal destacado con efecto visual NeonBorderBox
                NeonBorderBox(modifier = Modifier.fillMaxWidth(), color = VerdePrincipal) {
                    Button(
                        onClick = {
                            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                                // Pasamos el bloque de código final como una lambda (trailing lambda).
                                viewModel.register(name, email, password, selectedRole) {
                                    // Este código se ejecuta SOLO cuando el registro es exitoso (onSuccess)
                                    Toast.makeText(context, "¡Cuenta creada! Inicia sesión", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                            } else {
                                Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("REGISTRARSE", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}