package com.devsMarr.pos_galeriaemi.ui.presentation.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsMarr.pos_galeriaemi.data.repository.CategoryRepository
import com.devsMarr.pos_galeriaemi.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    // Variable privada para controlar la selección del usuario (null = Todas)
    private val _selectedCategoryId = MutableStateFlow<Long?>(null)

    // El flujo principal de Estado que la UI va a observar
    val uiState: StateFlow<InventoryUiState> = combine(
        productRepository.getAllActiveProducts(),
        categoryRepository.getAllCategories(),
        _selectedCategoryId
    ) { products, categories, selectedId ->

        // Lógica de filtrado: Si selectedId es null, pasa todo; si no, filtra.
        val filteredProducts = if (selectedId == null) {
            products
        } else {
            products.filter { it.categoryId == selectedId }
        }

        // Crea el objeto de estado final importado desde InventoryUiState.kt
        InventoryUiState(
            isLoading = false,
            categories = categories,
            products = filteredProducts,
            selectedCategoryId = selectedId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InventoryUiState(isLoading = true)
    )

    // --- Acciones de Usuario ---

    fun onCategorySelected(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
    }

    fun onDeleteProduct(productId: Long) {
        viewModelScope.launch {
            productRepository.deleteProduct(productId)
        }
    }
}