package com.devsMarr.pos_galeriaemi.ui.presentation.inventory

import com.devsMarr.pos_galeriaemi.domain.model.Category
import com.devsMarr.pos_galeriaemi.domain.model.Product

/**
 * Representa el estado de la pantalla de Inventario en un momento dado.
 * @param isLoading: Si estamos cargando datos iniciales.
 * @param categories: Lista de categorías para el carrusel superior.
 * @param products: Lista de productos (ya filtrados) para la lista inferior.
 * @param selectedCategoryId: ID de la categoría seleccionada (null = "Todas").
 */
data class InventoryUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val products: List<Product> = emptyList(),
    val selectedCategoryId: Long? = null
)