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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ecorescueapp.data.local.DonationEntity
import com.example.ecorescueapp.ui.components.DonationCard
import com.example.ecorescueapp.ui.components.InfoDialog
// IMPORTANTE: Aseguramos los imports de los componentes visuales
import com.example.ecorescueapp.ui.component.FloatingFoodBackground
import com.example.ecorescueapp.ui.component.NeonBorderBox
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
    // --- ESTADOS DE UI ---
    var showHelp by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    // Estados para diálogos y formularios
    var showCodeDialog by remember { mutableStateOf<DonationEntity?>(null) }
    var inputCode by remember { mutableStateOf("") }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    val context = LocalContext.current
    val donations by viewModel.donationList.collectAsState(initial = emptyList())
    val filter by viewModel.currentFilter.collectAsState()

    // Lanzadores de Cámara y Voz
    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            inputCode = result.contents
            Toast.makeText(context, "QR Detectado: ${result.contents}", Toast.LENGTH_SHORT).show()
        }
    }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val resultText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            if (resultText != null) { description = resultText }
        }
    }

    // Estilos de Input (Cyberpunk)
    val inputColors = OutlinedTextFieldDefaults.colors(
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
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("FoodShare", fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Admin", color = VerdePrincipal, fontWeight = FontWeight.Light)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D0D0D)),
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
                            text = { Text("Ver Informes", color = Color.White) },
                            onClick = {
                                menuExpanded = false
                                navController.navigate(Screen.Report.route)
                            },
                            leadingIcon = { Icon(Icons.Default.Assessment, null, tint = VerdePrincipal) }
                        )
                        DropdownMenuItem(
                            text = { Text("Ayuda", color = Color.White) },
                            onClick = {
                                menuExpanded = false
                                showHelp = true
                            },
                            leadingIcon = { Icon(Icons.Default.Info, null, tint = VerdePrincipal) }
                        )
                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.5f))
                        DropdownMenuItem(
                            text = { Text("Cerrar Sesión", color = AcentoNaranja) },
                            onClick = {
                                menuExpanded = false
                                navController.navigate(Screen.Login.route)
                            },
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = AcentoNaranja) }
                        )
                    }
                }
            )
        }
    ) { padding ->
        // RA4.g: Contenedor Box para el fondo animado (Estilo gráfico unificado)
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            // --- CAPA 1: FONDO DE PARTÍCULAS ---
            FloatingFoodBackground()

            // --- CAPA 2: CONTENIDO PRINCIPAL ---
            Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {

                Text(
                    "PUBLICAR OFERTA",
                    color = VerdePrincipal,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text("Producto (Ej: Pan)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = inputColors,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description, onValueChange = { description = it },
                    label = { Text("Descripción") },
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
                    colors = inputColors,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantity, onValueChange = { quantity = it },
                        label = { Text("Cantidad") },
                        modifier = Modifier.weight(1f),
                        colors = inputColors,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = imageUrl, onValueChange = { imageUrl = it },
                        label = { Text("URL Foto") },
                        modifier = Modifier.weight(1f),
                        colors = inputColors,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // RA4.g: Uso de efectos visuales (NeonBorderBox) para destacar acción crítica
                NeonBorderBox(
                    modifier = Modifier.fillMaxWidth(),
                    color = VerdePrincipal
                ) {
                    Button(
                        onClick = {
                            if (title.isNotEmpty() && quantity.isNotEmpty()) {
                                viewModel.addDonation(title, description, quantity, imageUrl)
                                title = ""; description = ""; quantity = ""; imageUrl = ""
                                Toast.makeText(context, "Publicado en FoodShare 🚀", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("PUBLICAR AHORA", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp), color = Color.DarkGray)

                // --- GESTIÓN DE STOCK ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("STOCK ACTUAL", color = Color.Gray, style = MaterialTheme.typography.labelMedium)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = filter == "TODOS", onClick = { viewModel.setFilter("TODOS") }, label = { Text("Todo") }, colors = FilterChipDefaults.filterChipColors(containerColor = Color(0xFF1E1E1E), labelColor = Color.White))
                        FilterChip(
                            selected = filter == "RESERVADOS",
                            onClick = { viewModel.setFilter("RESERVADOS") },
                            label = { Text("Alertas") },
                            leadingIcon = { if(filter=="RESERVADOS") Icon(Icons.Default.Warning, null, modifier = Modifier.size(16.dp)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AcentoNaranja, containerColor = Color(0xFF1E1E1E), labelColor = AcentoNaranja)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
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
                                Box(modifier = Modifier.fillMaxSize().padding(vertical = 4.dp).background(color, RoundedCornerShape(16.dp)), contentAlignment = Alignment.CenterEnd) {
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
        }

        // --- DIÁLOGOS (Manteniendo estética oscura) ---
        if (showHelp) {
            InfoDialog(title = "Ayuda Admin", desc = "• Dicta descripción con 🎙️.\n• Desliza para borrar.\n• Escanea el QR del cliente para validar.", onDismiss = { showHelp = false })
        }

        if (showCodeDialog != null) {
            AlertDialog(
                containerColor = Color(0xFF1E1E1E),
                onDismissRequest = { showCodeDialog = null },
                title = { Text("ENTREGAR PEDIDO 📦", color = AcentoNaranja, fontWeight = FontWeight.Bold) },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Cliente:", color = Color.Gray, fontSize = 12.sp)
                        Text(showCodeDialog?.reservedBy ?: "Anónimo", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón de Escáner
                        Button(
                            onClick = {
                                val options = ScanOptions()
                                options.setPrompt("Enfoca el QR del cliente")
                                options.setBeepEnabled(true)
                                options.setOrientationLocked(false)
                                scanLauncher.launch(options)
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            border = androidx.compose.foundation.BorderStroke(1.dp, VerdePrincipal)
                        ) {
                            Icon(Icons.Default.QrCodeScanner, null, tint = VerdePrincipal)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ESCANEAR QR (AR)", color = VerdePrincipal)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("--- o manual ---", color = Color.DarkGray, fontSize = 10.sp)

                        OutlinedTextField(
                            value = inputCode, onValueChange = { if (it.length <= 4) inputCode = it },
                            label = { Text("PIN") },
                            colors = inputColors,
                            singleLine = true,
                            modifier = Modifier.width(150.dp)
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
                    }, colors = ButtonDefaults.buttonColors(containerColor = AcentoNaranja)) { Text("VALIDAR", color = Color.Black) }
                },
                dismissButton = { TextButton(onClick = { showCodeDialog = null }) { Text("Cancelar", color = Color.Gray) } }
            )
        }
    }
}