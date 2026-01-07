package com.example.ecorescueapp.ui.screens

import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ecorescueapp.data.local.DonationEntity
import com.example.ecorescueapp.ui.components.DonationCard
import com.example.ecorescueapp.ui.components.InfoDialog
import com.example.ecorescueapp.ui.navigation.Screen
import com.example.ecorescueapp.ui.theme.AcentoNaranja
import com.example.ecorescueapp.ui.theme.VerdePrincipal
import com.example.ecorescueapp.ui.viewmodel.AdminViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    // --- ESTADOS DE LA INTERFAZ ---
    var showHelp by remember { mutableStateOf(false) }

    // Estados Validación PIN
    var showCodeDialog by remember { mutableStateOf<DonationEntity?>(null) }
    var inputCode by remember { mutableStateOf("") }

    // --- FORMULARIO DE ALTA (Nuevos campos) ---
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") } // Antes date
    var imageUrl by remember { mutableStateOf("") } // Nuevo campo URL

    val context = LocalContext.current
    val donations by viewModel.donationList.collectAsState(initial = emptyList())
    val filter by viewModel.currentFilter.collectAsState()

    // 1. LANZADOR DEL ESCÁNER QR (AR / Visión Artificial)
    // Esto abre la cámara para leer el código del voluntario
    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            // Si el escáner lee algo, lo ponemos en la caja de texto automáticamente
            inputCode = result.contents
            Toast.makeText(context, "QR Detectado: ${result.contents}", Toast.LENGTH_SHORT).show()
        }
    }

    // Lanzador Voz (Google Speech)
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val resultText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            if (resultText != null) { description = resultText }
        }
    }

    Scaffold(
        containerColor = Color(0xFF0D0D0D), // Fondo Negro FoodShare
        topBar = {
            TopAppBar(
                title = { Text("FoodShare Admin 🏪", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    actionIconContentColor = VerdePrincipal
                ),
                actions = {
                    IconButton(onClick = { showHelp = true }) { Icon(Icons.Default.Info, "Ayuda") }
                    IconButton(onClick = { navController.navigate(Screen.Report.route) }) { Icon(Icons.Default.Assessment, "Informes") }
                    IconButton(onClick = { navController.navigate(Screen.Login.route) }) { Icon(Icons.AutoMirrored.Filled.ExitToApp, "Salir") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {

            // --- SECCIÓN 1: FORMULARIO DE ALTA ---
            Text("NUEVA OFERTA", color = VerdePrincipal, style = MaterialTheme.typography.labelMedium)

            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Producto (Ej: Pan)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VerdePrincipal, unfocusedBorderColor = Color.Gray)
            )

            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Descripción (o dicta 🎙️)") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
                        }
                        try { speechLauncher.launch(intent) } catch (e: Exception) {
                            Toast.makeText(context, "Error micrófono", Toast.LENGTH_SHORT).show()
                        }
                    }) { Icon(Icons.Default.Mic, "Dictar", tint = VerdePrincipal) }
                },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VerdePrincipal, unfocusedBorderColor = Color.Gray)
            )

            // Fila para Cantidad y URL
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = quantity, onValueChange = { quantity = it },
                    label = { Text("Cantidad") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VerdePrincipal, unfocusedBorderColor = Color.Gray)
                )
                OutlinedTextField(
                    value = imageUrl, onValueChange = { imageUrl = it },
                    label = { Text("URL Foto") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VerdePrincipal, unfocusedBorderColor = Color.Gray)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (title.isNotEmpty() && quantity.isNotEmpty()) {
                        viewModel.addDonation(title, description, quantity, imageUrl)
                        title = ""; description = ""; quantity = ""; imageUrl = ""
                        Toast.makeText(context, "Publicado en FoodShare 🚀", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().shadow(8.dp, spotColor = VerdePrincipal),
                colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
            ) {
                Icon(Icons.Default.Add, null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("PUBLICAR OFERTA", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.DarkGray)

            // --- SECCIÓN 2: FILTROS (RA5.c) ---
            Text("GESTIÓN DE STOCK", color = Color.Gray, style = MaterialTheme.typography.labelMedium)

            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = filter == "TODOS", onClick = { viewModel.setFilter("TODOS") }, label = { Text("Todos") })
                FilterChip(selected = filter == "DISPONIBLES", onClick = { viewModel.setFilter("DISPONIBLES") }, label = { Text("Verdes") }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = VerdePrincipal))
                FilterChip(selected = filter == "RESERVADOS", onClick = { viewModel.setFilter("RESERVADOS") }, label = { Text("Rojos") }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AcentoNaranja))
            }

            // --- SECCIÓN 3: LISTA CON SWIPE (RA3.c) ---
            LazyColumn {
                items(donations, key = { it.id }) { item ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart || it == SwipeToDismissBoxValue.StartToEnd) {
                                viewModel.deleteDonation(item)
                                Toast.makeText(context, "Eliminado", Toast.LENGTH_SHORT).show()
                                true
                            } else false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) Color.Red.copy(alpha = 0.6f) else Color.Transparent
                            Box(modifier = Modifier.fillMaxSize().padding(vertical = 4.dp).background(color), contentAlignment = Alignment.CenterEnd) {
                                Icon(Icons.Default.Delete, "Borrar", tint = Color.White, modifier = Modifier.padding(end = 20.dp))
                            }
                        },
                        content = {
                            DonationCard(donation = item, isAdmin = true, onActionClick = {
                                showCodeDialog = item
                                inputCode = ""
                            })
                        }
                    )
                }
            }
        }

        // --- DIÁLOGOS ---
        if (showHelp) {
            InfoDialog(
                title = "Ayuda FoodShare ℹ️",
                desc = "• Dicta la descripción con 🎙️.\n• Desliza izq/der para borrar.\n• Para entregar: Pulsa 'Validar' y usa el ESCÁNER QR o escribe el PIN.",
                onDismiss = { showHelp = false }
            )
        }

        // --- DIÁLOGO DE VALIDACIÓN (ACTUALIZADO CON ESCÁNER) ---
        if (showCodeDialog != null) {
            AlertDialog(
                containerColor = Color(0xFF1A1A1A),
                onDismissRequest = { showCodeDialog = null },
                title = { Text("ENTREGAR PEDIDO 📦", color = AcentoNaranja) },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Cliente: ${showCodeDialog?.reservedBy ?: "Anónimo"}", color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))

                        // --- BOTÓN DE REALIDAD AUMENTADA (QR) ---
                        Button(
                            onClick = {
                                val options = ScanOptions()
                                options.setPrompt("Enfoca el QR del voluntario")
                                options.setBeepEnabled(true)
                                options.setOrientationLocked(false)
                                scanLauncher.launch(options)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                        ) {
                            Icon(Icons.Default.QrCodeScanner, null, tint = VerdePrincipal)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ESCANEAR QR (AR)", color = VerdePrincipal)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("- O escribe el PIN manualmente -", color = Color.Gray, style = MaterialTheme.typography.labelSmall)

                        OutlinedTextField(
                            value = inputCode, onValueChange = { if (it.length <= 4) inputCode = it },
                            label = { Text("Código PIN") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AcentoNaranja, unfocusedBorderColor = Color.Gray),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (viewModel.completeDonation(showCodeDialog!!, inputCode)) {
                            Toast.makeText(context, "¡Entrega Confirmada!", Toast.LENGTH_SHORT).show()
                            showCodeDialog = null
                        } else {
                            Toast.makeText(context, "PIN Incorrecto", Toast.LENGTH_SHORT).show()
                        }
                    }, colors = ButtonDefaults.buttonColors(containerColor = AcentoNaranja)) { Text("VALIDAR ENTREGA", color = Color.Black) }
                },
                dismissButton = { TextButton(onClick = { showCodeDialog = null }) { Text("Cancelar") } }
            )
        }
    }
}