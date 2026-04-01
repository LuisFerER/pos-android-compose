package com.devsMarr.pos_galeriaemi.ui.presentation.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsMarr.pos_galeriaemi.data.repository.ReportRepository
import com.devsMarr.pos_galeriaemi.data.repository.SettingsRepository
import com.devsMarr.pos_galeriaemi.domain.service.PdfCategoryData
import com.devsMarr.pos_galeriaemi.domain.service.PdfExportService
import com.devsMarr.pos_galeriaemi.domain.service.PdfProductData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DailyReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val pdfExportService: PdfExportService,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyReportUiState())
    val uiState: StateFlow<DailyReportUiState> = _uiState.asStateFlow()

    private val _pdfExportEvent = MutableSharedFlow<File>()
    val pdfExportEvent = _pdfExportEvent.asSharedFlow()

    private val _isExporting = MutableStateFlow(false)
    val isExporting = _isExporting.asStateFlow()

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

    fun exportToPdf() {
        viewModelScope.launch {
            _isExporting.value = true
            try {
                val currentState = _uiState.value

                // 👇 NUEVO: Forzamos Locale a Español (México) y mejoramos el formato a "02 de marzo de 2026"
                val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "MX"))
                val dateString = dateFormat.format(Date())
                val total = currentState.totalRevenue

                // 👇 NUEVO: Obtenemos la configuración del negocio
                val config = settingsRepository.appConfigFlow.first()
                val businessName = config.businessName.ifBlank { "Mi Negocio" } // Por si está vacío

                val detailedCategories = currentState.sales.map { saleCategory ->
                    val products = reportRepository.getDailySalesByProduct(
                        categoryId = saleCategory.categoryId,
                        startOfDay = getStartOfDay(),
                        endOfDay = getEndOfDay()
                    )

                    PdfCategoryData(
                        categoryName = saleCategory.categoryName,
                        categoryTotal = saleCategory.totalAmount,
                        products = products.map { p ->
                            PdfProductData(
                                quantityAndName = "${p.quantitySold.toInt()}x ${p.productName}",
                                subtotal = p.totalAmount
                            )
                        }
                    )
                }

                val generatedFile = pdfExportService.exportDailyReport(
                    businessName = businessName, // 👈 Se lo pasamos al PDF
                    dateString = dateString,
                    totalSales = total,
                    categories = detailedCategories
                )

                _pdfExportEvent.emit(generatedFile)

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al generar PDF: ${e.message}"
                )
            } finally {
                _isExporting.value = false
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