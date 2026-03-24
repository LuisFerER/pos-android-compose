package com.devsMarr.pos_galeriaemi.ui.presentation.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsMarr.pos_galeriaemi.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DailyReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyReportUiState())
    val uiState: StateFlow<DailyReportUiState> = _uiState.asStateFlow()

    init {
        loadDailyReport()
    }

    private fun loadDailyReport() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val startOfDay = getStartOfDay()
            val endOfDay = getEndOfDay()

            reportRepository.getDailySalesByCategory(startOfDay, endOfDay)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar reporte: ${e.message}"
                    )
                }
                .collect { salesList ->
                    val total = salesList.sumOf { it.totalAmount }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        sales = salesList,
                        totalRevenue = total,
                        errorMessage = null
                    )
                }
        }
    }

    // --- LÓGICA DEL ACORDEÓN ---
    fun toggleCategoryExpansion(categoryId: Long) {
        viewModelScope.launch {
            val currentState = _uiState.value

            // Si el usuario tocó la categoría que ya estaba abierta, la cerramos
            if (currentState.expandedCategoryId == categoryId) {
                _uiState.value = currentState.copy(
                    expandedCategoryId = null,
                    expandedCategoryProducts = emptyList()
                )
                return@launch
            }

            // Si es una nueva, ponemos el ID y mostramos que está cargando
            _uiState.value = currentState.copy(
                expandedCategoryId = categoryId,
                isLoadingProducts = true
            )

            // Vamos a la base de datos por los productos específicos
            try {
                val products = reportRepository.getDailySalesByProduct(
                    categoryId = categoryId,
                    startOfDay = getStartOfDay(),
                    endOfDay = getEndOfDay()
                )

                // Los guardamos en el estado para que la vista los pinte
                _uiState.value = _uiState.value.copy(
                    expandedCategoryProducts = products,
                    isLoadingProducts = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingProducts = false,
                    expandedCategoryProducts = emptyList()
                )
            }
        }
    }

    private fun getStartOfDay(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun getEndOfDay(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return calendar.timeInMillis
    }
}