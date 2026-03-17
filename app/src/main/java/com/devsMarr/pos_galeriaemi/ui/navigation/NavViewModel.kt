package com.devsMarr.pos_galeriaemi.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsMarr.pos_galeriaemi.data.repository.CashShiftRepository
import com.devsMarr.pos_galeriaemi.data.repository.TicketRepository
import com.devsMarr.pos_galeriaemi.domain.manager.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NavUiState(
    val showCloseShiftDialog: Boolean = false,
    val startingCash: Double = 0.0,
    val totalSales: Double = 0.0,
    val expectedAmount: Double = 0.0
)

@HiltViewModel
class NavViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val cashShiftRepository: CashShiftRepository, // Inyectamos repo de caja
    private val ticketRepository: TicketRepository        // Inyectamos repo de ventas
) : ViewModel() {

    // Observamos al usuario actual para dibujar su nombre y su rol en el menú lateral
    val currentUser = sessionManager.currentUser

    // Estado reactivo para mostrar u ocultar el diálogo de corte
    private val _uiState = MutableStateFlow(NavUiState())
    val uiState: StateFlow<NavUiState> = _uiState.asStateFlow()

    fun logout() {
        sessionManager.logout()
    }

    // --- LÓGICA DE CORTE DE CAJA ---

    // Iniciar el cálculo para mostrar el diálogo
    fun onIntentCloseShift() {
        viewModelScope.launch {
            val currentShift = cashShiftRepository.getCurrentOpenShift()

            if (currentShift != null) {
                // Obtenemos el total vendido de los tickets (Asegúrate de tener esta función en tu TicketRepository)
                val totalSales = ticketRepository.getTotalSalesForShiftClosure(currentShift.id)

                // Matemáticas: Fondo Inicial + Ventas Totales
                val expected = currentShift.startingCash + totalSales

                _uiState.value = _uiState.value.copy(
                    showCloseShiftDialog = true,
                    startingCash = currentShift.startingCash,
                    totalSales = totalSales,
                    expectedAmount = expected
                )
            }
        }
    }

    // Ocultar el diálogo si el usuario cancela
    fun hideCloseShiftDialog() {
        _uiState.value = _uiState.value.copy(showCloseShiftDialog = false)
    }

    // 3. Ejecutar el cierre real en la Base de Datos
    fun confirmCloseShift(actualAmount: Double, notes: String?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val user = sessionManager.getCurrentUser() ?: return@launch
            val expected = _uiState.value.expectedAmount

            // Llamamos a nuestro Repositorio para guardar el cierre con las matemáticas listas
            val result = cashShiftRepository.closeShift(
                userId = user.id,
                finalCashCalculated = expected,
                finalCashReal = actualAmount,
                notes = notes
            )

            if (result.isSuccess) {
                hideCloseShiftDialog()
                logout() // Cerramos la sesión del usuario al terminar el turno
                onSuccess() // Callback para indicar que debemos navegar al Login
            }
        }
    }
}