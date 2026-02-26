package com.devsMarr.pos_galeriaemi.ui.presentation.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsMarr.pos_galeriaemi.data.repository.CategoryRepository
import com.devsMarr.pos_galeriaemi.domain.model.Category
import com.devsMarr.pos_galeriaemi.domain.model.Product
import com.devsMarr.pos_galeriaemi.data.repository.ProductRepository
import com.devsMarr.pos_galeriaemi.data.repository.TicketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PosViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val ticketRepository: TicketRepository
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

        //TODO: DESCOMENTAR PARA CUANDO SE USE LA BD REAL

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
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        applyFilters()
    }

    fun searchProduct(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    private fun applyFilters() {
        val currentState = _uiState.value
        val categoryFilter = currentState.selectedCategory
        val textFilter = currentState.searchQuery

        val filteredList = fullProductList.filter { product ->
            val matchesCategory = categoryFilter == null || product.categoryId == categoryFilter.id
            val matchesText = textFilter.isBlank() || product.name.contains(textFilter, ignoreCase = true)
            matchesCategory && matchesText
        }

        // Actualizar el catálogo visible en la UI
        _uiState.value = currentState.copy(productsCatalog = filteredList)
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
            isPaymentSufficient = false,
            isSaleCompleted = false
        )
    }

    // --- ACCIONES DE COBRO ---

    fun onAmountReceivedChange(input: String) {
        val cleanInput = input.filter { it.isDigit() || it == '.' }

        val amount = cleanInput.toDoubleOrNull() ?: 0.0
        val total = _uiState.value.totalAmount

        val difference = amount - total
        val change = if (difference > 0) difference else 0.0

        val isSufficient = amount >= total && total > 0

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
            try {
                val domainDetails = currentState.cartItems.map { cartItem ->
                    com.devsMarr.pos_galeriaemi.domain.model.TicketDetail(
                        productId = cartItem.product?.id ?: -1L,
                        productNameSnapshot = cartItem.name,
                        unitPriceSnapshot = cartItem.price,
                        quantity = cartItem.quantity,
                        subtotal = cartItem.quantity * cartItem.price
                    )
                }

                // Armamos el Ticket completo
                val ticket = com.devsMarr.pos_galeriaemi.domain.model.Ticket(
                    shiftId = 1L, // TODO: Cambiar por el ID del turno activo
                    totalAmount = currentState.totalAmount,
                    receivedAmount = currentState.amountReceivedInput.toDoubleOrNull() ?: currentState.totalAmount,
                    changeAmount = currentState.changeDue,
                    paymentMethod = "CASH",
                    status = "COMPLETED",
                    details = domainDetails
                )

                // Mandamos a guardar a la Base de Datos
                val generatedTicketId = ticketRepository.saveSale(ticket)

                // Se notifica a la UI que la venta se registró correctamente
                _uiState.value = _uiState.value.copy(
                    isSaleCompleted = true
                )

            } catch (e: Exception) {
                e.printStackTrace()
                android.util.Log.e("POS_DEBUG", "ERROR AL GUARDAR VENTA: ${e.message}")
            }
        }
    }

    fun acknowledgeSaleCompleted() {
        // Reestablecemos el estado de la venta
        clearCart()
    }
}