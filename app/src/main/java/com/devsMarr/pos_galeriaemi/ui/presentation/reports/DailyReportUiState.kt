package com.devsMarr.pos_galeriaemi.ui.presentation.reports

import com.devsMarr.pos_galeriaemi.domain.model.CategoryDailySale
import com.devsMarr.pos_galeriaemi.domain.model.ProductDailySale

data class DailyReportUiState(
    val isLoading: Boolean = true,
    val sales: List<CategoryDailySale> = emptyList(),
    val totalRevenue: Double = 0.0,
    val errorMessage: String? = null,

    // --- ESTADO DEL ACORDEÓN ---
    val expandedCategoryId: Long? = null, // Guarda el ID de la fila abierta (null si todo está cerrado)
    val expandedCategoryProducts: List<ProductDailySale> = emptyList(), // Lista de productos de esa fila
    val isLoadingProducts: Boolean = false // Por si tarda un microsegundo en cargar de la BD
)