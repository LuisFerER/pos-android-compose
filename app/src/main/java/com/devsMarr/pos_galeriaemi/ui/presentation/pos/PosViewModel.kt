package com.devsMarr.pos_galeriaemi.ui.presentation.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsMarr.pos_galeriaemi.data.repository.CategoryRepository
import com.devsMarr.pos_galeriaemi.domain.model.Category
import com.devsMarr.pos_galeriaemi.domain.model.Product
import com.devsMarr.pos_galeriaemi.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PosViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PosUiState())
    val uiState: StateFlow<PosUiState> = _uiState.asStateFlow()

    // Cache local
    private var fullProductList: List<Product> = emptyList()

    init {
        subscribeToCatalog()
    }

    private fun subscribeToCatalog() {
        // Indicar al UI que se estan cargando datos
        _uiState.value = _uiState.value.copy(isLoadingCatalog = true)

        viewModelScope.launch {
            launch {
                categoryRepository.getAllCategories().collect { categoriesList ->
                    _uiState.value = _uiState.value.copy(
                        categories = categoriesList
                    )
                }
            }

            launch {
                productRepository.getAllActiveProducts().collect { productsList ->
                    fullProductList = productsList
                    val currentCategory = _uiState.value.selectedCategory

                    val listToShow = if (currentCategory == null) {
                        productsList
                    } else {
                        productsList.filter { it.categoryId == currentCategory.id }
                    }

                    _uiState.value = _uiState.value.copy(
                        productsCatalog = listToShow,
                        isLoadingCatalog = false
                    )
                }
            }
        }
    }

    // --- ACCIONES DE CATÁLOGO ---

    fun filterByCategory(category: Category?) {
        val filteredList = if (category == null) {
            fullProductList
        } else {
            fullProductList.filter { it.categoryId == category.id }
        }

        _uiState.value = _uiState.value.copy(
            productsCatalog = filteredList,
            selectedCategory = category
        )
    }

    fun searchProduct(query: String) {
        val currentCategory = _uiState.value.selectedCategory

        if (query.isBlank()) {
            filterByCategory(currentCategory)
            return
        }

        val searchResult = fullProductList.filter { product ->
            val matchesCategory = currentCategory == null || product.categoryId == currentCategory.id
            val matchesName = product.name.contains(query, ignoreCase = true)
            matchesCategory && matchesName
        }

    }

    // --- ACCIONES DE CARRITO ---

    fun addToCart(product: Product) {
        val currentCart = _uiState.value.cartItems.toMutableList()
        val existingItemIndex = currentCart.indexOfFirst { it.product?.id == product.id }
        if (existingItemIndex != -1) {
            val existingItem = currentCart[existingItemIndex]
            currentCart[existingItemIndex] = existingItem.copy(
                quantity = existingItem.quantity + 1.0
            )
        }
        else {
            val newItem = CartItem(
                id = System.currentTimeMillis(),
                product = product,
                name = product.name,
                price = product.price,
                quantity = 1.0
            )
            currentCart.add(newItem
            )
        }

        val newTotal = currentCart.sumOf { it.totalLine }

        _uiState.value = _uiState.value.copy(
            cartItems = currentCart,
            totalAmount = newTotal
        )
    }

    fun addManualAmount(amount: Double, description: String = "Varios") {
        val currentCart = _uiState.value.cartItems.toMutableList()

        // Creamos un item sin 'product' porque no existe en el catálogo
        val newItem = CartItem(
            id = System.currentTimeMillis(),
            product = null,
            name = description,
            price = amount,
            quantity = 1.0
        )

        currentCart.add(newItem)
        val newTotal = currentCart.sumOf { it.totalLine }

        _uiState.value = _uiState.value.copy(
            cartItems = currentCart,
            totalAmount = newTotal
        )
    }

    fun updateQuantity(cartItemId: Long, newQuantity: Double) {
        if (newQuantity <= 0.0) {
            removeFromCart(cartItemId)
            return
        }

        val currentCart = _uiState.value.cartItems.toMutableList()

        val itemIndex = currentCart.indexOfFirst { it.id == cartItemId }

        if (itemIndex != -1) {
            val existingItem = currentCart[itemIndex]

            currentCart[itemIndex] = existingItem.copy(
                quantity = newQuantity
            )

            val newTotal = currentCart.sumOf { it.totalLine }

            _uiState.value = _uiState.value.copy(
                cartItems = currentCart,
                totalAmount = newTotal
            )
        }
    }

    fun updateItemPrice(cartItemId: Long, newPrice: Double) {
        val currentCart = _uiState.value.cartItems.toMutableList()
        val itemIndex = currentCart.indexOfFirst { it.id == cartItemId }

        if (itemIndex != -1) {
            val existingItem = currentCart[itemIndex]

            currentCart[itemIndex] = existingItem.copy(
                price = newPrice
            )

            val newTotal = currentCart.sumOf { it.totalLine }

            _uiState.value = _uiState.value.copy(
                cartItems = currentCart,
                totalAmount = newTotal
            )
        }
    }

    fun removeFromCart(cartItemId: Long) {
        // Filtramos la lista dejando todos MENOS el que tenga ese ID
        val currentCart = _uiState.value.cartItems.filterNot { it.id == cartItemId }
        val newTotal = currentCart.sumOf { it.totalLine }

        _uiState.value = _uiState.value.copy(
            cartItems = currentCart,
            totalAmount = newTotal
        )
    }

    fun clearCart() {
        _uiState.value = _uiState.value.copy(
            cartItems = emptyList(),
            totalAmount = 0.0,
            amountReceivedInput = "",
            changeDue = 0.0,
            isPaymentSufficient = false
        )
    }

    // --- ACCIONES DE COBRO ---

    fun onAmountReceivedChange(input: String) {
        val cleanInput = input.filter { it.isDigit() || it == '.' }

        val amount = cleanInput.toDoubleOrNull() ?: 0.0
        val total = _uiState.value.totalAmount

        val difference = amount - total
        val change = if (difference > 0) difference else 0.0

        val isSufficient = amount >= total && total > 0 // También evitamos cobrar $0.0

        _uiState.value = _uiState.value.copy(
            amountReceivedInput = cleanInput,
            changeDue = change,
            isPaymentSufficient = isSufficient
        )
    }

    fun finalizeSale() {
        val currentState = _uiState.value

        // Doble validación de seguridad
        if (currentState.cartItems.isEmpty() || !currentState.isPaymentSufficient) {
            return
        }

        viewModelScope.launch {
            // TODO:
            // 1. Generar un ID o Número de Ticket
            // 2. Crear TicketHeadEntity con el Total y la Fecha
            // 3. Mapear los cartItems a TicketDetailEntity
            // 4. Llamar a ticketRepository.saveTicket(head, details)

            // 2. Notificamos a la UI que la venta se registró correctamente
            _uiState.value = _uiState.value.copy(
                isSaleCompleted = true
            )
        }
    }

    // Llama a esta función desde la UI cuando cierres el diálogo de "Venta Exitosa"
    fun acknowledgeSaleCompleted() {
        // Restablecemos el estado de la venta
        clearCart()
    }
}