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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info // Icono de ayuda
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ecorescueapp.ui.components.DonationCard
import com.example.ecorescueapp.ui.components.InfoDialog // <--- ¡IMPORTANTE! Asegúrate de tener este archivo creado
import com.example.ecorescueapp.ui.navigation.Screen
import com.example.ecorescueapp.ui.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    // Estado para el diálogo de ayuda
    var showHelp by remember { mutableStateOf(false) }

    // Variables del formulario
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Listas y Filtros
    val donations by viewModel.donationList.collectAsState(initial = emptyList())
    val filter by viewModel.currentFilter.collectAsState()

    // Lanzador de Voz
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data = result.data
            val resultText = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            if (resultText != null) {
                description = resultText
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Comercio 🏪") },
                actions = {
                    // Botón Ayuda (RA6)
                    IconButton(onClick = { showHelp = true }) {
                        Icon(Icons.Default.Info, contentDescription = "Ayuda")
                    }
                    // Botón Informes (RA5)
                    IconButton(onClick = { navController.navigate(Screen.Report.route) }) {
                        Icon(Icons.Default.Assessment, contentDescription = "Ver Informes")
                    }
                    // Botón Salir
                    IconButton(onClick = { navController.navigate(Screen.Login.route) }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Salir")
                    }
                }
            )
        }
    ) { padding ->
        // Contenido Principal
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // --- FORMULARIO DE ALTA ---
            Text("Nueva Donación", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Producto (Ej: Pan)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción (o dicta 🎙️)") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Describe el producto...")
                        }
                        try {
                            speechLauncher.launch(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: Tu móvil no soporta voz", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.Mic, contentDescription = "Dictar")
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Caducidad (Ej: Hoy 20:00)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (title.isNotEmpty() && description.isNotEmpty()) {
                        viewModel.addDonation(title, description, date, "Panadería Pepe")
                        Toast.makeText(context, "Donación Añadida", Toast.LENGTH_SHORT).show()
                        title = ""
                        description = ""
                        date = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Publicar Donación")
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // --- FILTROS (RA5.c) ---
            Text("Mis Donaciones Activas", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filter == "TODOS",
                    onClick = { viewModel.setFilter("TODOS") },
                    label = { Text("Todos") },
                    leadingIcon = if (filter == "TODOS") {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else null
                )

                FilterChip(
                    selected = filter == "DISPONIBLES",
                    onClick = { viewModel.setFilter("DISPONIBLES") },
                    label = { Text("Verdes") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFE8F5E9)
                    )
                )

                FilterChip(
                    selected = filter == "RESERVADOS",
                    onClick = { viewModel.setFilter("RESERVADOS") },
                    label = { Text("Rojos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFFEBEE)
                    )
                )
            }

            // --- LISTA ---
            LazyColumn {
                items(donations, key = { it.id }) { item ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart || it == SwipeToDismissBoxValue.StartToEnd) {
                                viewModel.deleteDonation(item)
                                Toast.makeText(context, "Eliminado", Toast.LENGTH_SHORT).show()
                                true
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
                                Color.Red.copy(alpha = 0.8f) else Color.Transparent

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 4.dp)
                                    .background(color),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Borrar",
                                    tint = Color.White,
                                    modifier = Modifier.padding(end = 20.dp)
                                )
                            }
                        },
                        content = {
                            DonationCard(donation = item)
                        }
                    )
                }
            }
        }

        // --- DIÁLOGO DE AYUDA (Aquí es donde se muestra) ---
        if (showHelp) {
            InfoDialog(
                title = "Ayuda del Panel ℹ️",
                desc = "• Usa el micrófono 🎙️ para dictar descripciones de productos.\n" +
                        "• Desliza una tarjeta para borrarla, tanto izquierda como derecha.\n" +
                        "• Usa los chips Verde(DISPONIBLES) / Rojo(RESERVADOS) para filtrar pedidos.",
                onDismiss = { showHelp = false }
            )
        }
    }
}