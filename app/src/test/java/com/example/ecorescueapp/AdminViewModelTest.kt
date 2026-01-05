package com.example.ecorescueapp

import com.example.ecorescueapp.data.local.DonationEntity
import com.example.ecorescueapp.data.repository.EcoRepository
import com.example.ecorescueapp.ui.viewmodel.AdminViewModel
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class AdminViewModelTest {

    // Activamos la regla que creaste antes para manejar hilos
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `getStats cuenta correctamente disponibles y reservados`() {
        // 1. PREPARACIÓN (GIVEN)
        // Creamos una lista falsa con: 2 disponibles (verde) y 1 reservado (rojo)
        val fakeDonations = listOf(
            DonationEntity(title = "Pan", isReserved = false, description = "", expirationDate = "", donorName = ""),
            DonationEntity(title = "Manzanas", isReserved = false, description = "", expirationDate = "", donorName = ""),
            DonationEntity(title = "Leche", isReserved = true, description = "", expirationDate = "", donorName = "")
        )

        // Creamos un Repositorio Falso (Mock) que devuelva nuestra lista
        val mockRepo = mock<EcoRepository> {
            on { getAllDonations() } doReturn flowOf(fakeDonations)
        }

        // Iniciamos el ViewModel con el repo falso
        val viewModel = AdminViewModel(mockRepo)

        // 2. EJECUCIÓN (WHEN)
        // Probamos la función matemática
        val stats = viewModel.getStats(fakeDonations)

        // 3. VERIFICACIÓN (THEN)
        // Le preguntamos al test: "¿Ha contado 2 disponibles?"
        assertEquals(2, stats.first)

        // "¿Ha contado 1 reservado?"
        assertEquals(1, stats.second)

        println("TEST PASADO: Detectados ${stats.first} disponibles y ${stats.second} reservados.")
    }
}