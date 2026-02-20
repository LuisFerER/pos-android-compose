package com.devsMarr.pos_galeriaemi.ui.presentation.product_form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsMarr.pos_galeriaemi.data.repository.CategoryRepository
import com.devsMarr.pos_galeriaemi.data.repository.ProductRepository
import com.devsMarr.pos_galeriaemi.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductFormViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle // Permite recibir el ID si venimos de la pantalla "Editar"
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductFormUiState())
    val uiState: StateFlow<ProductFormUiState> = _uiState.asStateFlow()

    init {
        loadCategories()

        // Si nos pasan un ID por la navegación, cargamos ese producto para editarlo
        val productId = savedStateHandle.get<Long>("productId")
        if (productId != null && productId != -1L) {
            loadProduct(productId)
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categoriesList ->
                _uiState.update {
                    it.copy(categories = categoriesList, isCategoriesLoading = false)
                }
            }
        }
    }

    private fun loadProduct(id: Long) {
        viewModelScope.launch {
            val product = productRepository.getProductById(id)
            product?.let { p ->
                _uiState.update { state ->
                    state.copy(
                        id = p.id,
                        name = p.name,
                        price = p.price.toString(),
                        stock = p.stock.toString(),
                        categoryId = p.categoryId,
                        isVariablePrice = p.isVariablePrice
                    )
                }
            }
        }
    }

    // --- Funciones para que la UI actualice el estado en tiempo real ---
    fun onNameChange(name: String) = _uiState.update { it.copy(name = name, errorMessage = null) }

    fun onPriceChange(price: String) {
        // Solo permitimos números y un punto decimal
        if (price.isEmpty() || price.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(price = price, errorMessage = null) }
        }
    }

    fun onStockChange(stock: String) {
        if (stock.isEmpty() || stock.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(stock = stock, errorMessage = null) }
        }
    }

    fun onCategoryChange(categoryId: Long) = _uiState.update { it.copy(categoryId = categoryId, errorMessage = null) }

    fun onVariablePriceChange(isVariable: Boolean) = _uiState.update { it.copy(isVariablePrice = isVariable) }

    // --- Guardar en la Base de Datos ---
    fun saveProduct() {
        val currentState = _uiState.value

        // Validaciones
        if (currentState.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El nombre no puede estar vacío") }
            return
        }
        if (currentState.categoryId == null) {
            _uiState.update { it.copy(errorMessage = "Debes seleccionar una categoría") }
            return
        }

        if (currentState.price.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El precio no puede estar vacío") }
            return
        }

        val priceDouble = currentState.price.toDoubleOrNull() ?: 0.0
        val stockDouble = currentState.stock.toDoubleOrNull() ?: 0.0

        // Proceso de guardado
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            val product = Product(
                id = currentState.id ?: 0L,
                name = currentState.name.trim(),
                price = priceDouble,
                stock = stockDouble,
                categoryId = currentState.categoryId,
                isVariablePrice = currentState.isVariablePrice,
                isActive = true
            )

            if (currentState.id == null || currentState.id == 0L) {
                productRepository.insertProduct(product)
            } else {
                productRepository.updateProduct(product)
            }

            // Avisamos a la UI que fue un éxito
            _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }
}