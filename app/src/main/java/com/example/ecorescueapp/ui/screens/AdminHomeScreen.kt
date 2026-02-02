package com.example.ecorescueapp.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
val NeonGreen = Color(0xFF4ADE80)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // --- ESTADOS ---
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

    // --- 1. LANZADOR DE RECONOCIMIENTO DE VOZ (NUEVO) ---
    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!results.isNullOrEmpty()) {
                // Añadimos el texto dictado a la descripción existente
                val text = results[0]
                description = if (description.isEmpty()) text else "$description $text"
            }
        }
    }

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

    // --- DIÁLOGOS ---
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
        val isReserved = !itemToDelete?.reservedBy.isNullOrEmpty()

        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color(0xFF1E1E1E),
            title = {
                Text(
                    text = if (isReserved) "⚠️ ¡PRODUCTO RESERVADO!" else "¿Eliminar Oferta?",
                    color = if (isReserved) AcentoNaranja else Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isReserved)
                        "El cliente '${itemToDelete?.reservedBy}' ya ha reservado este producto. Si lo eliminas, se cancelará su pedido. ¿Estás seguro?"
                    else
                        "Se eliminará '${itemToDelete?.title}' permanentemente del listado.",
                    color = Color.Gray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDonation(itemToDelete!!)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("BORRAR DEFINITIVAMENTE", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar", color = Color.Gray) }
            }
        )
    }

    if (showHelp) {
        InfoDialog(
            title = "Ayuda Admin",
            desc = "Usa las pestañas superiores para cambiar entre crear ofertas y gestionar las existentes. Desliza una tarjeta a la izquierda para borrarla.",
            onDismiss = { showHelp = false }
        )
    }

    // --- ESTRUCTURA PRINCIPAL DE LA PANTALLA ---
    Scaffold(
        containerColor = Color(0xFF0D0D0D),
        topBar = {
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
                        DropdownMenuItem(
                            text = { Text("Historial de Pedidos", color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.History, null, tint = Color.White) },
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

                Spacer(modifier = Modifier.height(8.dp))
                CustomToggleBar(
                    isPublishMode = !showListMode,
                    onToggle = { isPublishing -> showListMode = !isPublishing }
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (!showListMode) {
                    // --- MODO PUBLICAR ---

                    Text(
                        text = if (donationToEdit == null) "Nueva Oferta" else "Editar Oferta",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    GlassTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = "Producto (Ej: Pan)",
                        icon = Icons.Default.Restaurant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- 2. CAMPO DESCRIPCIÓN CON VOZ CONECTADA ---
                    GlassTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = "Descripción",
                        icon = null,
                        trailingIcon = Icons.Default.Mic,
                        // Aquí conectamos el click con el lanzador de voz
                        onTrailingIconClick = {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Describe el producto...")
                            }
                            try {
                                voiceLauncher.launch(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "No se admite entrada de voz", Toast.LENGTH_SHORT).show()
                            }
                        },
                        singleLine = false,
                        modifier = Modifier.height(120.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GlassTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        placeholder = "Cantidad",
                        icon = Icons.Default.Layers,
                        keyboardType = KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GlassTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        placeholder = "URL Foto",
                        icon = Icons.Default.CameraAlt
                    )

                    Spacer(modifier = Modifier.weight(1f))

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
                                title = ""; description = ""; quantity = ""; imageUrl = ""; donationToEdit = null
                                showListMode = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(12.dp, RoundedCornerShape(50), spotColor = NeonGreen),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        shape = RoundedCornerShape(50)
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
                    // --- MODO GESTIONAR ---
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(donationList, key = { it.id }) { donation ->

                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { value ->
                                    if (value == SwipeToDismissBoxValue.EndToStart) {
                                        itemToDelete = donation
                                        showDeleteDialog = true
                                        return@rememberSwipeToDismissBoxState false
                                    }
                                    false
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = false,
                                enableDismissFromEndToStart = true,
                                backgroundContent = {
                                    val backgroundColor = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                                        Color.Red.copy(alpha = 0.8f)
                                    } else {
                                        Color.Transparent
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(backgroundColor)
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Eliminar",
                                                tint = Color.White
                                            )
                                        }
                                    }
                                },
                                content = {
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
                                            showListMode = false
                                        }
                                    )
                                }
                            )
                        }
                    }

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
// --- COMPONENTES VISUALES ---
// -------------------------------------------------------------------------

/**
 * Campo de texto estilo "Glass".
 * 3. AHORA ACEPTA onTrailingIconClick para el micro.
 */
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null, // <--- NUEVO PARÁMETRO
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
            {
                // Si pasamos una acción, es un botón, si no, solo un icono decorativo
                if (onTrailingIconClick != null) {
                    IconButton(onClick = onTrailingIconClick) {
                        Icon(trailingIcon, contentDescription = "Dictar", tint = Color.LightGray)
                    }
                } else {
                    Icon(trailingIcon, contentDescription = null, tint = Color.LightGray)
                }
            }
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
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
    )
}

@Composable
fun CustomToggleBar(
    isPublishMode: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(0xFF2A2A2A))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
                .background(if (isPublishMode) GlassBackground.copy(alpha = 0.3f) else Color.Transparent)
                .clickable { onToggle(true) }
        ) {
            Text(
                "Publicar",
                color = if (isPublishMode) NeonGreen else Color.Gray,
                fontWeight = if (isPublishMode) FontWeight.Bold else FontWeight.Normal
            )
        }

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