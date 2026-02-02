package com.example.ecorescueapp.ui.screens

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
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
import com.example.ecorescueapp.ui.component.NeonBorderBox
import com.example.ecorescueapp.ui.components.FloatingParticles
import com.example.ecorescueapp.ui.navigation.Screen
import com.example.ecorescueapp.ui.theme.VerdePrincipal
import com.example.ecorescueapp.ui.viewmodel.LoginViewModel
import com.example.ecorescueapp.utils.BiometricAuth

/**
 * Defino la pantalla de inicio de sesión de mi aplicación.
 * En esta pantalla, gestiono la entrada de credenciales del usuario,
 * la autenticación biométrica y la navegación a las pantallas principales
 * después de un inicio de sesión exitoso.
 *
 * @param navController El controlador de navegación que utilizo para moverme entre pantallas.
 * @param viewModel El ViewModel que contiene la lógica de negocio para el inicio de sesión.
 */
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    /**
     * Declaro los estados para el correo electrónico, la contraseña y la disponibilidad
     * de la autenticación biométrica. Utilizo `remember` y `mutableStateOf`
     * para que la UI se recomponga automáticamente cuando estos valores cambien.
     */
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var canUseBiometric by remember { mutableStateOf(false) }
    val context = LocalContext.current

    /**
     * Utilizo `LaunchedEffect` para comprobar si la autenticación biométrica está
     * disponible en el dispositivo tan pronto como se compone la pantalla.
     * Esto lo hago solo una vez gracias a `Unit` como clave.
     */
    LaunchedEffect(Unit) { canUseBiometric = BiometricAuth.canAuthenticate(context) }

    /**
     * Construyo la UI principal de la pantalla. Un `Box` que ocupa toda la pantalla
     * con un fondo oscuro y un efecto de comida flotante para un diseño atractivo.
     */
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
    ) {
        // partículas flotantes
        FloatingParticles(
            particleCount = 20,
            color = VerdePrincipal.copy(alpha = 0.35f)
        )

        FloatingFoodBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            /**
             * Muestro el logo de mi aplicación. He diseñado un `Box` circular con
             * una sombra de color neón y un borde para que destaque.
             */
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .shadow(20.dp, spotColor = VerdePrincipal, shape = CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFF1E1E1E))
                    .border(2.dp, VerdePrincipal, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null, // El icono es puramente decorativo.
                    tint = VerdePrincipal,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            /**
             * Muestro el nombre de la aplicación y el eslogan. Utilizo diferentes
             * estilos de texto para crear una jerarquía visual.
             */
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

            /**
             * Defino un estilo personalizado para los `OutlinedTextField` para que
             * coincidan con la estética de neón y oscuridad de la aplicación.
             */
            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VerdePrincipal,
                unfocusedBorderColor = Color.DarkGray,
                focusedLabelColor = VerdePrincipal,
                unfocusedLabelColor = Color.Gray,
                cursorColor = VerdePrincipal,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color(0xFF1E1E1E),
                unfocusedContainerColor = Color(0xFF1E1E1E)
            )

            /**
             * Campo de texto para que el usuario introduzca su email.
             * Se actualiza el estado `email` en cada cambio.
             */
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Corporativo") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            /**
             * Campo de texto para la contraseña. Utilizo `PasswordVisualTransformation`
             * para ocultar los caracteres que el usuario escribe.
             */
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            /**
             * Este es mi botón de inicio de sesión principal. Lo he envuelto en un
             * `NeonBorderBox` para darle un efecto brillante. Al hacer clic,
             * invoco el método `login` del ViewModel.
             */
            NeonBorderBox(
                modifier = Modifier.fillMaxWidth(),
                color = VerdePrincipal
            ) {
                Button(
                    onClick = {
                        viewModel.login(email, password,
                            onLoginSuccess = { role ->
                                /**
                                 * Si el inicio de sesión es exitoso, navego a la pantalla
                                 * correspondiente según el rol del usuario (Admin o User).
                                 */
                                navController.navigate(if (role == "ADMIN") Screen.AdminHome.route else Screen.UserHome.route)
                            },
                            onError = {
                                /**
                                 * Si hay un error, muestro un mensaje Toast.
                                 */
                                Toast.makeText(context, "Error credenciales", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("INICIAR SESIÓN", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            /**
             * Muestro el botón de autenticación biométrica solo si el dispositivo
             * lo soporta.
             */
            if (canUseBiometric) {
                OutlinedButton(
                    onClick = {
                        val activity = context.findActivity()
                        if (activity != null) {
                            /**
                             * Inicio el proceso de autenticación biométrica.
                             * Navego a la pantalla de administrador si tiene éxito.
                             */
                            BiometricAuth.authenticate(activity,
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
            /**
             * Un botón de texto que permite al usuario navegar a la pantalla de registro
             * para crear una nueva cuenta.
             */
            TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                Text("Crear cuenta nueva", color = Color.Gray)
            }

            Spacer(modifier = Modifier.weight(1f))

            /**
             * He añadido un botón de desarrollo para cargar datos de demostración
             * en la base de datos. Esto me facilita las pruebas.
             */
            TextButton(onClick = { viewModel.seedDatabase(); Toast.makeText(context, "Datos cargados", Toast.LENGTH_SHORT).show() }) {
                Text("🛠️ Cargar Datos Demo", color = Color.DarkGray, fontSize = 12.sp)
            }
        }
    }
}

/**
 * He creado esta función de extensión para encontrar la `FragmentActivity`
 * a partir de un `Context`. Es necesaria para poder mostrar el diálogo
 * de autenticación biométrica.
 *
 * @return La `FragmentActivity` si se encuentra, o `null` en caso contrario.
 */
fun Context.findActivity(): FragmentActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is FragmentActivity) return context
        context = context.baseContext
    }
    return null
}
