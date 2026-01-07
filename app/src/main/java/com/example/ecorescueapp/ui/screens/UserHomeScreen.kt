package com.example.ecorescueapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ecorescueapp.ui.component.FloatingFoodBackground
import com.example.ecorescueapp.ui.components.DonationCard
import com.example.ecorescueapp.ui.components.InfoDialog
import com.example.ecorescueapp.ui.navigation.Screen
import com.example.ecorescueapp.ui.theme.AcentoNaranja
import com.example.ecorescueapp.ui.theme.VerdePrincipal
import com.example.ecorescueapp.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(
    navController: NavController,
    viewModel: UserViewModel = hiltViewModel()
) {
    val donations by viewModel.donations.collectAsState(initial = emptyList())
    var showHelp by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFF0D0D0D),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("FoodShare", fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Voluntario", color = Color.Cyan, fontWeight = FontWeight.Light)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D0D0D)),
                actions = {
                    // RA4.c: Menú contextual para agrupar acciones secundarias
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menú", tint = Color.White)
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(Color(0xFF1E1E1E))
                    ) {
                        DropdownMenuItem(
                            text = { Text("Mis Pedidos", color = Color.White) },
                            onClick = {
                                menuExpanded = false
                                navController.navigate(Screen.UserHistory.route)
                            },
                            leadingIcon = { Icon(Icons.Default.History, null, tint = Color.Cyan) }
                        )
                        DropdownMenuItem(
                            text = { Text("Ayuda", color = Color.White) },
                            onClick = {
                                menuExpanded = false
                                showHelp = true
                            },
                            leadingIcon = { Icon(Icons.Default.Info, null, tint = Color.Cyan) }
                        )
                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.5f))
                        DropdownMenuItem(
                            text = { Text("Salir", color = AcentoNaranja) },
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
        // Contenedor principal con Z-Ordering para el fondo animado
        Box(modifier = Modifier.fillMaxSize()) {

            // --- CAPA 1: FONDO ANIMADO ---
            FloatingFoodBackground()

            // --- CAPA 2: CONTENIDO SCROLLABLE ---
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                item {
                    Column(modifier = Modifier.padding(bottom = 24.dp)) {
                        Text(
                            "Hola, Voluntario",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Ayuda a salvar comida hoy.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }

                    Text(
                        "DISPONIBLE AHORA",
                        color = VerdePrincipal,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (donations.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Text("No hay ofertas disponibles ahora mismo.", color = Color.Gray)
                        }
                    }
                }

                items(donations) { item ->
                    // Reutilización del componente DonationCard (RA3.b)
                    DonationCard(
                        donation = item,
                        isAdmin = false,
                        onActionClick = { viewModel.reserveDonation(item) }
                    )
                }
            }
        }

        // RA6.c: Ayuda contextual en forma de diálogo
        if (showHelp) {
            InfoDialog(
                title = "Ayuda Voluntario",
                desc = "• Pulsa 'Reservar' en los productos.\n• Recibirás un QR y PIN.\n• Muéstralo en el comercio para recoger.",
                onDismiss = { showHelp = false }
            )
        }
    }
}