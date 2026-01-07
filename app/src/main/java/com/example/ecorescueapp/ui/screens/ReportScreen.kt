package com.example.ecorescueapp.ui.screens

import android.content.Context
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ecorescueapp.ui.viewmodel.AdminViewModel
import android.content.ContentValues
import android.content.Intent
import android.provider.MediaStore
import com.example.ecorescueapp.ui.component.FloatingFoodBackground
import com.example.ecorescueapp.ui.component.PieChart
import com.example.ecorescueapp.ui.theme.AcentoNaranja
import com.example.ecorescueapp.ui.theme.VerdePrincipal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    // RA5.d: Recojo el flujo de estadísticas (Triple) del ViewModel
    val stats by viewModel.getStatsFlow().collectAsState(initial = Triple(0, 0, 0))
    val available = stats.first
    val reserved = stats.second
    val completed = stats.third
    val total = available + reserved + completed

    val context = LocalContext.current

    Scaffold(
        containerColor = Color(0xFF0D0D0D),
        topBar = {
            TopAppBar(
                title = { Text("Informe de Impacto 📈", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D0D0D),
                    titleContentColor = Color.White,
                    navigationIconContentColor = VerdePrincipal
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        // RA4.g: Integro el fondo animado para mantener consistencia estética
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            FloatingFoodBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tarjeta Resumen Premium
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("BALANCE TOTAL", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                            Text("$total UNIDADES", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Black)
                        }
                        Text("📊", style = MaterialTheme.typography.displaySmall)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // RA5.e: Gráfico Circular Mejorado (3 Estados)
                Box(contentAlignment = Alignment.Center) {
                    PieChart(available = available, reserved = reserved, completed = completed)

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ÉXITO", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(
                            text = "${if(total > 0) (completed * 100 / total).toInt() else 0}%",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Leyenda de colores
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LegendItem(color = VerdePrincipal, label = "Stock", value = available.toString())
                    LegendItem(color = AcentoNaranja, label = "Reservado", value = reserved.toString())
                    LegendItem(color = Color(0xFF2196F3), label = "Vendido", value = completed.toString())
                }

                Spacer(modifier = Modifier.weight(1f))

                // RA5.b: Botón para generación de informes PDF
                Button(
                    onClick = { generatePDF(context, available, reserved, completed, total) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("EXPORTAR INFORME PDF", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(modifier = Modifier.size(12.dp), shape = CircleShape, color = color) {}
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

// RA5.a: Establezco la estructura del informe PDF
fun generatePDF(context: Context, av: Int, res: Int, comp: Int, total: Int) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint()

    // Cabecera
    paint.color = AndroidColor.BLACK
    paint.textSize = 18f
    paint.isFakeBoldText = true
    canvas.drawText("INFORME FOODSHARE", 40f, 50f, paint)

    paint.textSize = 12f
    paint.isFakeBoldText = false
    canvas.drawText("Fecha: 07/01/2026", 20f, 80f, paint)
    canvas.drawText("------------------------------------------------", 20f, 100f, paint)

    // Datos detallados
    canvas.drawText("Desglose de Impacto:", 20f, 130f, paint)

    paint.color = AndroidColor.BLUE
    canvas.drawText("• Ventas Completadas: $comp", 30f, 160f, paint)

    paint.color = AndroidColor.rgb(255, 165, 0) // Naranja
    canvas.drawText("• En Proceso (Reservas): $res", 30f, 185f, paint)

    paint.color = AndroidColor.rgb(0, 128, 0) // Verde
    canvas.drawText("• Stock Disponible: $av", 30f, 210f, paint)

    paint.color = AndroidColor.BLACK
    canvas.drawText("Total Gestionado: $total u.", 20f, 250f, paint)

    pdfDocument.finishPage(page)

    // Guardado en almacenamiento (RA6.b)
    val filename = "FoodShare_Report_${System.currentTimeMillis()}.pdf"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }

    try {
        val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { output ->
                pdfDocument.writeTo(output)
            }
            Toast.makeText(context, "PDF Guardado en Descargas", Toast.LENGTH_LONG).show()

            // Intent para abrir el PDF
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(it, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(Intent.createChooser(intent, "Ver Informe"))
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Error generando PDF", Toast.LENGTH_SHORT).show()
    } finally {
        pdfDocument.close()
    }
}