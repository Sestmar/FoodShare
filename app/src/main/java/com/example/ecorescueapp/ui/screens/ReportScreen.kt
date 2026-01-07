
package com.example.ecorescueapp.ui.screens

import android.content.Context
import android.graphics.Color as AndroidColor // Alias para evitar conflicto con Compose Color
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
import com.example.ecorescueapp.ui.component.PieChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    // Obtenemos estadísticas en tiempo real del historial
    val stats by viewModel.getStatsFlow().collectAsState(initial = Pair(0, 0))
    val available = stats.first
    val completed = stats.second
    val total = available + completed

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Informe de Impacto 📈") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding -> // <-- La llave estaba mal cerrada aquí
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tarjeta de Resumen con diseño profesional
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Balance Total", style = MaterialTheme.typography.labelLarge)
                        Text("$total unidades", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    }
                    Text("📊", style = MaterialTheme.typography.displaySmall)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Gráfico Circular Animado
            Box(contentAlignment = Alignment.Center) {
                PieChart(available = available, completed = completed)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ÉXITO", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(
                        text = "${if(total > 0) (completed * 100 / total).toInt() else 0}%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Leyenda de colores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(color = Color(0xFF4CAF50), label = "Disponibles", value = available.toString())
                LegendItem(color = Color(0xFF2196F3), label = "Ventas", value = completed.toString())
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { generatePDF(context, available, completed, total) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Exportar Informe PDF")
            }
        }
    }
}

// Componente para la leyenda del gráfico
@Composable
fun LegendItem(color: Color, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(modifier = Modifier.size(12.dp), shape = CircleShape, color = color) {}
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
    }
}

fun generatePDF(context: Context, av: Int, comp: Int, total: Int) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint()

    // Configuración del texto del PDF
    paint.color = AndroidColor.BLACK
    paint.textSize = 16f
    paint.isFakeBoldText = true
    canvas.drawText("INFORME OFICIAL FOODSHARE", 40f, 50f, paint)

    paint.textSize = 12f
    paint.isFakeBoldText = false
    canvas.drawText("Fecha: 06/01/2026", 20f, 80f, paint)
    canvas.drawText("------------------------------------------------", 20f, 100f, paint)

    canvas.drawText("Análisis de resultados:", 20f, 130f, paint)
    canvas.drawText("• Total gestionado: $total unidades", 30f, 160f, paint)
    canvas.drawText("• Ventas con éxito: $comp", 30f, 185f, paint)
    canvas.drawText("• Stock disponible: $av", 30f, 210f, paint)

    // Barra de progreso visual en el PDF
    paint.color = AndroidColor.LTGRAY
    canvas.drawRect(20f, 250f, 280f, 260f, paint)
    paint.color = AndroidColor.BLUE
    val progress = if(total > 0) (comp.toFloat() / total.toFloat()) * 260f else 0f
    canvas.drawRect(20f, 250f, 20f + progress, 260f, paint)

    paint.color = AndroidColor.BLACK
    canvas.drawText("Tasa de éxito: ${if(total>0) (comp*100/total) else 0}%", 20f, 280f, paint)

    pdfDocument.finishPage(page)

    val filename = "Reporte_FoodShare_${System.currentTimeMillis()}.pdf"
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
            Toast.makeText(context, "PDF generado correctamente", Toast.LENGTH_SHORT).show()

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(it, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(Intent.createChooser(intent, "Abrir informe"))
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Error al generar PDF", Toast.LENGTH_SHORT).show()
    } finally {
        pdfDocument.close()
    }
}
