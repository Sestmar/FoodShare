package com.example.ecorescueapp

import com.example.ecorescueapp.data.local.DonationEntity
import com.example.ecorescueapp.data.repository.EcoRepository
import com.example.ecorescueapp.ui.viewmodel.AdminViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class AdminViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `getStatsFlow calcula correctamente disponibles y completados`() = runTest {
        // 1. GIVEN (Preparación)
        // Creamos datos falsos con la NUEVA estructura (Quantity, ImageUrl, isCompleted)
        val fakeHistory = listOf(
            // Disponibles (Ni reservados ni completados)
            DonationEntity(title = "Pan", quantity = "1", description = "", donorName = "", isReserved = false, isCompleted = false),
            DonationEntity(title = "Agua", quantity = "1", description = "", donorName = "", isReserved = false, isCompleted = false),

            // Completado (Venta finalizada)
            DonationEntity(title = "Leche", quantity = "1", description = "", donorName = "", isReserved = true, isCompleted = true),

            // Reservado pero no completado (En tránsito)
            DonationEntity(title = "Arroz", quantity = "1", description = "", donorName = "", isReserved = true, isCompleted = false)
        )

        // Mock del repositorio devolviendo el flujo
        val mockRepo = mock<EcoRepository> {
            on { getAllHistory() } doReturn flowOf(fakeHistory)
            // Necesario para que el init del ViewModel no falle
            on { getActiveDonations() } doReturn flowOf(emptyList())
        }

        val viewModel = AdminViewModel(mockRepo)

        // 2. WHEN (Ejecución)
        // Obtenemos el primer valor emitido por el flujo de estadísticas
        val stats = viewModel.getStatsFlow().first()

        // 3. THEN (Verificación)
        // Disponibles esperados: 2 (Pan, Agua)
        assertEquals("Debe haber 2 disponibles", 2, stats.first)

        // Completados esperados: 1 (Leche) -> El gráfico de tarta muestra ÉXITO
        assertEquals("Debe haber 1 completado", 1, stats.second)

        println("✅ TEST ADMIN PASADO: Stats calculadas correctamente.")
    }
}