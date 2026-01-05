package com.example.ecorescueapp.ui.screens

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ecorescueapp.ui.viewmodel.AdminViewModel
import java.io.File
import java.io.FileOutputStream
import android.content.ContentValues
import android.content.Intent
import android.provider.MediaStore
import com.google.ai.client.generativeai.common.shared.Content

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val donations by viewModel.donationList.collectAsState(initial = emptyList())
    val stats = viewModel.getStats(donations)
    val available = stats.first
    val reserved = stats.second
    val total = available + reserved
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Informe de Impacto 📈") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- CUMPLIMIENTOS RA5 (Cálculos y Totales) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Resumen Mensual", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("📦 Total Donaciones: $total")
                    Text("🟢 Disponibles: $available")
                    Text("🔴 Reservadas: $reserved") // RA5.d Valores calculados
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- CUMPLIMIENTOS RA5.e (Gráficos) ---
            Text("Distribución de Ayuda", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // Dibujamos un gráfico de barras simple con Canvas
            Canvas(modifier = Modifier.size(300.dp, 200.dp)) {
                val barWidth = size.width / 3
                val maxBarHeight = size.height

                // Escalar altura según el total (evitar dividir por 0)
                val scale = if (total > 0) maxBarHeight / total else 0f

                // Barra Disponibles (Verde)
                drawRect(
                    color = androidx.compose.ui.graphics.Color.Green,
                    topLeft = Offset(x = 50f, y = maxBarHeight - (available * scale)),
                    size = Size(barWidth, available * scale)
                )

                // Barra Reservados (Rojo)
                drawRect(
                    color = androidx.compose.ui.graphics.Color.Red,
                    topLeft = Offset(x = 50f + barWidth + 20f, y = maxBarHeight - (reserved * scale)),
                    size = Size(barWidth, reserved * scale)
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Text("Disponibles", color = androidx.compose.ui.graphics.Color.Green)
                Text("Reservados", color = androidx.compose.ui.graphics.Color.Red)
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- CUMPLIMIENTOS RA5.b (Generar Informe PDF) ---
            Button(
                onClick = {
                    generatePDF(context, available, reserved, total)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generar PDF Oficial")
            }
        }
    }
}

// Función auxiliar para generar PDF en la carpeta PÚBLICA DE DESCARGAS
fun generatePDF(context: Context, av: Int, res: Int, total: Int) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
    val page = pdfDocument.startPage(pageInfo)

    val canvas = page.canvas
    val paint = Paint()

    // --- DIBUJO DEL PDF ---
    paint.color = Color.BLACK
    paint.textSize = 14f
    canvas.drawText("INFORME ECO-RESCUE", 80f, 50f, paint)

    paint.textSize = 12f
    canvas.drawText("Fecha: 05/01/2026", 20f, 80f, paint)
    canvas.drawText("--------------------------------", 20f, 100f, paint)
    canvas.drawText("Total Donaciones: $total", 20f, 130f, paint)
    canvas.drawText("Comida Salvada (Kg est.): ${total * 2}", 20f, 150f, paint)
    canvas.drawText(" - Disponibles: $av", 20f, 180f, paint)
    canvas.drawText(" - Reservados: $res", 20f, 200f, paint)

    paint.color = Color.GREEN
    canvas.drawRect(20f, 250f, 20f + (av * 10), 270f, paint)
    paint.color = Color.RED
    canvas.drawRect(20f, 280f, 20f + (res * 10), 300f, paint)

    pdfDocument.finishPage(page)

    // --- GUARDADO ---
    val filename = "Informe_EcoRescue_${System.currentTimeMillis()}.pdf"

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }

    try {
        val collection = MediaStore.Files.getContentUri("external")
        val uri = context.contentResolver.insert(collection, contentValues)

        if (uri != null) {
            val outputStream = context.contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                pdfDocument.writeTo(outputStream)
                outputStream.close()

                // MENSAJE DE ÉXITO
                Toast.makeText(context, "PDF generado correctamente ✅", Toast.LENGTH_SHORT).show()

                // --- APERTURA AUTOMÁTICA (LA CLAVE) ---
                val openIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
                }

                // Verificamos si hay alguna app para abrir PDF
                val chooser = Intent.createChooser(openIntent, "Abrir Informe con...")
                try {
                    context.startActivity(chooser)
                } catch (e: Exception) {
                    Toast.makeText(context, "No tienes app para ver PDFs 😢", Toast.LENGTH_LONG).show()
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
    } finally {
        pdfDocument.close()
    }
}