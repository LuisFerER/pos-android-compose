package com.devsMarr.pos_galeriaemi.ui.presentation.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsMarr.pos_galeriaemi.data.repository.CategoryRepository
import com.devsMarr.pos_galeriaemi.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)

    // --- Estado para manejar mensajes de error ---
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val uiState: StateFlow<InventoryUiState> = combine(
        productRepository.getAllActiveProducts(),
        categoryRepository.getAllCategories(),
        _selectedCategoryId
    ) { products, categories, selectedId ->
        val filteredProducts = if (selectedId == null) {
            products
        } else {
            products.filter { it.categoryId == selectedId }
        }

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

    fun onCategorySelected(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
    }

    fun onDeleteProduct(productId: Long) {
        viewModelScope.launch {
            productRepository.deleteProduct(productId)
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            try {
                categoryRepository.deleteCategory(categoryId)

                if (_selectedCategoryId.value == categoryId) {
                    _selectedCategoryId.value = null
                }
            } catch (e: Exception) {
                // --- En lugar de fallar en silencio, mandamos el mensaje a la UI ---
                _errorMessage.value = "No se puede eliminar esta categoría porque aún tiene productos registrados. Elimina o reasigna los productos primero."
            }
        }
    }

    // --- Función para limpiar el error cuando el usuario cierre la alerta ---
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}