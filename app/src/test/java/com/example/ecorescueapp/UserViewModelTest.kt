package com.example.ecorescueapp

import com.example.ecorescueapp.data.local.DonationEntity
import com.example.ecorescueapp.data.repository.EcoRepository
import com.example.ecorescueapp.ui.viewmodel.UserViewModel
import com.example.ecorescueapp.utils.CurrentUser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class UserViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `el usuario solo ve sus propios pedidos en el historial`() = runTest {
        // 1. GIVEN
        val historyMix = listOf(
            // Pedido de JUAN
            DonationEntity(title = "Pizza", quantity="1", description="", donorName="", reservedBy = "Juan", isReserved = true),
            // Pedido de PEDRO
            DonationEntity(title = "Sopa", quantity="1", description="", donorName="", reservedBy = "Pedro", isReserved = true)
        )

        val mockRepo = mock<EcoRepository> {
            on { getAllHistory() } doReturn flowOf(historyMix)
            on { getActiveDonations() } doReturn flowOf(emptyList())
        }

        // SIMULAMOS LOGIN COMO JUAN
        CurrentUser.activeUser = "Juan"

        val viewModel = UserViewModel(mockRepo)

        // 2. WHEN
        val myOrders = viewModel.allHistory.first()

        // 3. THEN
        // La lista debe tener solo 1 elemento (la Pizza de Juan)
        assertEquals("El usuario debería ver solo 1 pedido", 1, myOrders.size)
        assertEquals("El pedido debería ser Pizza", "Pizza", myOrders[0].title)

        println("✅ TEST USUARIO PASADO: Filtro de privacidad funciona.")
    }
}