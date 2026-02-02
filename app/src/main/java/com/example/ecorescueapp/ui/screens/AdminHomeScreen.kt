package com.example.ecorescueapp.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ecorescueapp.data.local.DonationEntity
import com.example.ecorescueapp.ui.component.FloatingFoodBackground
import com.example.ecorescueapp.ui.components.DonationCard
import com.example.ecorescueapp.ui.components.FloatingParticles
import com.example.ecorescueapp.ui.components.InfoDialog
import com.example.ecorescueapp.ui.navigation.Screen
import com.example.ecorescueapp.ui.theme.AcentoNaranja
import com.example.ecorescueapp.ui.viewmodel.AdminViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

// Definimos los colores específicos del boceto
val GlassBackground = Color.White.copy(alpha = 0.1f)
val GlassBorder = Color.White.copy(alpha = 0.2f)
val TextHintColor = Color.LightGray
val NeonGreen = Color(0xFF4ADE80) // Un verde más vibrante para el botón principal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // --- ESTADOS (Lógica Original Intacta) ---
    val donationList by viewModel.donationList.collectAsState(initial = emptyList())

    // Formulario
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var donationToEdit by remember { mutableStateOf<DonationEntity?>(null) }

    // UI Control
    var showListMode by remember { mutableStateOf(false) } // False = Publicar, True = Gestionar
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<DonationEntity?>(null) }
    var showValidateDialog by remember { mutableStateOf(false) }
    var itemToValidate by remember { mutableStateOf<DonationEntity?>(null) }
    var inputPin by remember { mutableStateOf("") }
    var showHelp by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    // --- ESCÁNER QR ---
    val scanLauncher = rememberLauncherForActivityResult(contract = ScanContract()) { result ->
        if (result.contents != null) {
            val scannedCode = result.contents
            if (itemToValidate != null) {
                val success = viewModel.completeDonation(itemToValidate!!, scannedCode)
                if (success) {
                    Toast.makeText(context, "✅ ¡QR Válido! Entrega completada.", Toast.LENGTH_LONG).show()
                    showValidateDialog = false
                    inputPin = ""
                    itemToValidate = null
                } else {
                    Toast.makeText(context, "❌ El QR no coincide.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // --- DIÁLOGOS (Respetando lógica, estética mínima oscura) ---
    if (showValidateDialog && itemToValidate != null) {
        AlertDialog(
            onDismissRequest = { showValidateDialog = false },
            containerColor = Color(0xFF1E1E1E),
            title = { Text("ENTREGAR PEDIDO", color = NeonGreen, fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Cliente: ${itemToValidate?.reservedBy}", color = Color.White)
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = {
                            val options = ScanOptions().apply {
                                setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                                setPrompt("Enfoca el código QR")
                                setBeepEnabled(true)
                                setOrientationLocked(false)
                            }
                            scanLauncher.launch(options)
                        },
                        border = BorderStroke(1.dp, NeonGreen),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.QrCodeScanner, null)
                        Spacer(Modifier.width(8.dp))
                        Text("ESCANEAR QR")
                    }
                    Spacer(Modifier.height(16.dp))
                    GlassTextField(
                        value = inputPin,
                        onValueChange = { inputPin = it },
                        placeholder = "O introduce PIN manual",
                        icon = Icons.Default.Lock
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (viewModel.completeDonation(itemToValidate!!, inputPin)) {
                            Toast.makeText(context, "¡Entrega Confirmada!", Toast.LENGTH_LONG).show()
                            showValidateDialog = false
                            inputPin = ""
                            itemToValidate = null
                        } else {
                            Toast.makeText(context, "PIN Incorrecto", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                ) { Text("VALIDAR", color = Color.Black) }
            },
            dismissButton = {
                TextButton(onClick = { showValidateDialog = false }) { Text("Cancelar", color = Color.Gray) }
            }
        )
    }

    if (showDeleteDialog && itemToDelete != null) {
        // Lógica de borrado original...
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color(0xFF1E1E1E),
            title = { Text("¿Eliminar?", color = Color.White) },
            text = { Text("Se eliminará '${itemToDelete?.title}' permanentemente.", color = Color.Gray) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDonation(itemToDelete!!)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("BORRAR", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar", color = Color.Gray) }
            }
        )
    }

    if (showHelp) {
        InfoDialog(
            title = "Ayuda Admin",
            desc = "Usa las pestañas superiores para cambiar entre crear ofertas y gestionar las existentes.",
            onDismiss = { showHelp = false }
        )
    }

    // --- ESTRUCTURA PRINCIPAL DE LA PANTALLA ---
    Scaffold(
        containerColor = Color(0xFF0D0D0D), // Fondo base muy oscuro
        topBar = {
            // TopBar transparente para que se vea el fondo
            TopAppBar(
                title = { Text("FoodShare Admin", color = Color.White.copy(alpha = 0.9f), fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menú", tint = Color.White)
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(Color(0xFF1E1E1E))
                    ) {
                        // opciones del menñu desplegable (ADMIN)
                        DropdownMenuItem(
                            text = { Text("Historial de Pedidos", color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.History, null, tint = Color.White) }, // Icono opcional
                            onClick = {
                                menuExpanded = false
                                navController.navigate(Screen.AdminHistory.route)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Informe de Impacto", color = Color.White) },
                            onClick = {
                                menuExpanded = false
                                navController.navigate(Screen.Report.route)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Ayuda", color = Color.Cyan) },
                            onClick = {
                                menuExpanded = false
                                showHelp = true
                            }
                        )
                        HorizontalDivider(color = Color.Gray)
                        DropdownMenuItem(
                            text = { Text("Salir", color = AcentoNaranja, fontWeight = FontWeight.Bold) },
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = AcentoNaranja) },
                            onClick = {
                                menuExpanded = false
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {

            FloatingParticles(
                particleCount = 25,
                color = NeonGreen.copy(alpha = 0.7f)
            )

            FloatingFoodBackground()

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- TOGGLE SUPERIOR (Publicar | Gestionar) ---
                Spacer(modifier = Modifier.height(8.dp))
                CustomToggleBar(
                    isPublishMode = !showListMode,
                    onToggle = { isPublishing -> showListMode = !isPublishing }
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (!showListMode) {
                    // --- MODO PUBLICAR (Estética del Boceto) ---

                    // Título Grande
                    Text(
                        text = if (donationToEdit == null) "Nueva Oferta" else "Editar Oferta",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Input: Producto
                    GlassTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = "Producto (Ej: Pan)",
                        icon = Icons.Default.Restaurant // Icono de comida
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input: Descripción (Con icono de microfono)
                    GlassTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = "Descripción",
                        icon = null, // No icono a la izquierda
                        trailingIcon = Icons.Default.Mic, // Icono a la derecha
                        singleLine = false,
                        modifier = Modifier.height(120.dp) // Más alto
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input: Cantidad
                    GlassTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        placeholder = "Cantidad",
                        icon = Icons.Default.Layers,
                        keyboardType = KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input: URL Foto
                    GlassTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        placeholder = "URL Foto",
                        icon = Icons.Default.CameraAlt
                    )

                    Spacer(modifier = Modifier.weight(1f)) // Empuja el botón al fondo

                    // --- BOTÓN GRANDE VERDE ---
                    Button(
                        onClick = {
                            if (title.isNotEmpty() && quantity.isNotEmpty()) {
                                if (donationToEdit == null) {
                                    viewModel.addDonation(title, description, quantity, imageUrl)
                                    Toast.makeText(context, "Oferta Publicada", Toast.LENGTH_SHORT).show()
                                } else {
                                    val updated = donationToEdit!!.copy(
                                        title = title, description = description, quantity = quantity, imageUrl = imageUrl
                                    )
                                    viewModel.updateDonation(updated)
                                    Toast.makeText(context, "Editado", Toast.LENGTH_SHORT).show()
                                }
                                // Limpieza
                                title = ""; description = ""; quantity = ""; imageUrl = ""; donationToEdit = null
                                showListMode = true // Ir a la lista al terminar
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(12.dp, RoundedCornerShape(50), spotColor = NeonGreen), // Resplandor
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        shape = RoundedCornerShape(50) // Botón pastilla
                    ) {
                        Text(
                            text = if (donationToEdit == null) "+ PUBLICAR AHORA" else "GUARDAR CAMBIOS",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    if (donationToEdit != null) {
                        TextButton(onClick = {
                            donationToEdit = null; title = ""; description = ""; quantity = ""; imageUrl = ""
                        }) {
                            Text("Cancelar Edición", color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                } else {
                    // --- MODO GESTIONAR (LISTA) ---
                    // Mantenemos la lista funcional pero limpia
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(donationList, key = { it.id }) { donation ->
                            // Reutilizamos tu DonationCard existente
                            DonationCard(
                                donation = donation,
                                isAdmin = true,
                                onActionClick = {
                                    itemToValidate = donation
                                    showValidateDialog = true
                                },
                                onEditClick = {
                                    title = donation.title
                                    description = donation.description
                                    quantity = donation.quantity
                                    imageUrl = donation.imageUrl ?: ""
                                    donationToEdit = donation
                                    showListMode = false // Volver al formulario
                                }
                            )
                        }
                    }

                    // Floating Action Button para volver a publicar si estamos en lista
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                        FloatingActionButton(
                            onClick = { showListMode = false },
                            containerColor = NeonGreen,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Añadir", tint = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// --- COMPONENTES VISUALES PERSONALIZADOS (ESTILO DESIGN IA) ---
// -------------------------------------------------------------------------

/**
 * Campo de texto estilo "Glass" (Vidrio):
 * Fondo translúcido, bordes muy redondeados (Pill shape), icono integrado.
 */
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextHintColor) },
        leadingIcon = if (icon != null) {
            { Icon(icon, contentDescription = null, tint = Color.LightGray) }
        } else null,
        trailingIcon = if (trailingIcon != null) {
            { Icon(trailingIcon, contentDescription = null, tint = Color.LightGray) }
        } else null,
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = GlassBackground,
            unfocusedContainerColor = GlassBackground,
            disabledContainerColor = GlassBackground,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = NeonGreen,
            focusedIndicatorColor = Color.Transparent, // Sin línea inferior
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(24.dp), // Bordes muy redondeados
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp)) // Borde sutil
    )
}

/**
 * Barra de pestañas personalizada estilo "Segmented Control".
 * Fondo gris oscuro, pastilla verde que se mueve (simulada por color).
 */
@Composable
fun CustomToggleBar(
    isPublishMode: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(50)) // Forma completa de pastilla
            .background(Color(0xFF2A2A2A)) // Fondo gris oscuro del contenedor
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón Publicar
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
                .background(if (isPublishMode) GlassBackground.copy(alpha = 0.3f) else Color.Transparent) // Highlight sutil
                .clickable { onToggle(true) }
        ) {
            Text(
                "Publicar",
                color = if (isPublishMode) NeonGreen else Color.Gray,
                fontWeight = if (isPublishMode) FontWeight.Bold else FontWeight.Normal
            )
        }

        // Botón Gestionar
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
                .background(if (!isPublishMode) GlassBackground.copy(alpha = 0.3f) else Color.Transparent)
                .clickable { onToggle(false) }
        ) {
            Text(
                "Gestionar",
                color = if (!isPublishMode) NeonGreen else Color.Gray,
                fontWeight = if (!isPublishMode) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}