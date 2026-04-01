package com.devsMarr.pos_galeriaemi.ui.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsMarr.pos_galeriaemi.data.repository.TicketRepository
import com.devsMarr.pos_galeriaemi.domain.model.Ticket
import com.devsMarr.pos_galeriaemi.domain.service.ExcelExportService
import java.io.File
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

data class TicketHistoryUiState(
    val tickets: List<Ticket> = emptyList(),
    val isLoading: Boolean = true,
    val startDateMillis: Long = System.currentTimeMillis(),
    val endDateMillis: Long = System.currentTimeMillis()
)

@HiltViewModel
class TicketHistoryViewModel @Inject constructor(
    private val ticketRepository: TicketRepository,
    private val excelExportService: ExcelExportService
) : ViewModel() {

    private val _uiState = MutableStateFlow(TicketHistoryUiState())
    val uiState: StateFlow<TicketHistoryUiState> = _uiState.asStateFlow()

    private val _exportedFile = MutableStateFlow<File?>(null)
    val exportedFile: StateFlow<File?> = _exportedFile.asStateFlow()

    private var allTicketsCache: List<Ticket> = emptyList()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            ticketRepository.getAllTicketsHistory().collect { ticketList ->
                allTicketsCache = ticketList

                // Filtramos para mostrar solo los del rango de fechas seleccionado en la UI
                val startLocal = getLocalStartOfDay(_uiState.value.startDateMillis)
                val endLocal = getLocalEndOfDay(_uiState.value.endDateMillis)
                filterTicketsByDateRange(startLocal, endLocal)
            }
        }
    }

    // Esta función la llamará la Vista al seleccionar un rango en el calendario
    fun updateSelectedDateRange(startUtcMillis: Long?, endUtcMillis: Long?) {
        if (startUtcMillis == null) return

        // Si el usuario solo tocó un día y no seleccionó un fin, usamos ese mismo día como fin
        val finalEndUtcMillis = endUtcMillis ?: startUtcMillis

        // Convertimos a hora local asegurando que cubra desde las 00:00:00 del inicio hasta las 23:59:59 del fin
        val startLocal = convertUtcToLocalStartOfDay(startUtcMillis)
        val endLocal = convertUtcToLocalEndOfDay(finalEndUtcMillis)

        filterTicketsByDateRange(startLocal, endLocal)
    }

    private fun filterTicketsByDateRange(startMillis: Long, endMillis: Long) {
        val filteredList = allTicketsCache.filter { ticket ->
            ticket.timestamp in startMillis..endMillis
        }

        _uiState.update { currentState ->
            currentState.copy(
                tickets = filteredList,
                isLoading = false,
                startDateMillis = startMillis,
                endDateMillis = endMillis
            )
        }
    }

    fun exportCurrentHistory() {
        viewModelScope.launch {
            val currentTickets = _uiState.value.tickets

            if (currentTickets.isNotEmpty()) {
                // Genera el archivo
                val file = excelExportService.exportHistoryToExcel(currentTickets)
                // Le avisa a la UI que ya está listo
                _exportedFile.value = file
            }
        }
    }

    fun clearExportedFile() {
        _exportedFile.value = null
    }

    // --- Funciones de Utilidad para Tiempos ---
    private fun getLocalStartOfDay(millis: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun getLocalEndOfDay(millis: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    private fun convertUtcToLocalStartOfDay(utcMillis: Long): Long {
        val calendarUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = utcMillis }
        return Calendar.getInstance().apply {
            set(calendarUTC.get(Calendar.YEAR), calendarUTC.get(Calendar.MONTH), calendarUTC.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun convertUtcToLocalEndOfDay(utcMillis: Long): Long {
        val calendarUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = utcMillis }
        return Calendar.getInstance().apply {
            set(calendarUTC.get(Calendar.YEAR), calendarUTC.get(Calendar.MONTH), calendarUTC.get(Calendar.DAY_OF_MONTH), 23, 59, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }
}