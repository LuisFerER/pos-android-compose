package com.devsMarr.pos_galeriaemi.ui.presentation.pos

import com.devsMarr.pos_galeriaemi.domain.model.Category
import com.devsMarr.pos_galeriaemi.domain.model.Product

data class CartItem(
    val id: Long = System.currentTimeMillis(), // ID temporal único para la lista
    val product: Product?,
    val name: String,
    val price: Double,
    val quantity: Double,
) {
    // Calculo del total de la linea
    val totalLine: Double
        get() = price * quantity
}

data class PosUiState (
    // --- SECCIÓN CATÁLOGO ---
    val productsCatalog: List<Product> = emptyList(), // Lista limpia de dominio
    val categories: List<Category> = emptyList(),     // Lista limpia de dominio
    val selectedCategory: Category? = null,           // Filtro activo (null = Todos)
    val isLoadingCatalog: Boolean = false,

    // --- SECCIÓN CARRITO ---
    val cartItems: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,

    // --- SECCIÓN COBRO ---
    val amountReceivedInput: String = "", // String para manejar el teclado numérico en pantalla
    val changeDue: Double = 0.0,
    val isPaymentSufficient: Boolean = false,
    val isSaleCompleted: Boolean = false
)