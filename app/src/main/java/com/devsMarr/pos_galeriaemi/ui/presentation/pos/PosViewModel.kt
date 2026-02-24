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

//        // ==========================================================
//        // CÓDIGO MOCK TEMPORAL PARA PRUEBAS UI
//        // ==========================================================
//        val mockCategories = getMockCategories()
//        val mockProducts = getMockProducts()
//
//        fullProductList = mockProducts // Guardamos en caché para las búsquedas
//
//        _uiState.value = _uiState.value.copy(
//            categories = mockCategories,
//            productsCatalog = mockProducts,
//            isLoadingCatalog = false
//        )
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
            try {
                // Convertimos los CartItems del carrito a TicketDetails (Dominio)
                val domainDetails = currentState.cartItems.map { cartItem ->
                    com.devsMarr.pos_galeriaemi.domain.model.TicketDetail(
                        // Como tienes productos manuales (sin ID), si es null le ponemos 0L
                        productId = cartItem.product?.id ?: 0L,
                        productNameSnapshot = cartItem.name,
                        unitPriceSnapshot = cartItem.price,
                        quantity = cartItem.quantity,
                        subtotal = cartItem.quantity * cartItem.price
                    )
                }

                // Armamos el Ticket completo (Dominio)
                val ticket = com.devsMarr.pos_galeriaemi.domain.model.Ticket(
                    shiftId = 1L, // TODO: Cambiar por el ID del turno activo cuando tengas ese módulo
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
                    // Podrías guardar el generatedTicketId aquí en el estado si lo necesitas para imprimir
                )

            } catch (e: Exception) {
                // Opcional: Manejar el error si falla la base de datos
                e.printStackTrace()
                println("❌ ERROR AL GUARDAR VENTA: ${e.message}")
            }
        }
    }

    fun acknowledgeSaleCompleted() {
        // Reestablecemos el estado de la venta
        clearCart()
    }

    // ==========================================================
    // TODO: ELIMINAR ESTAS FUNCIONES CUANDO SE USE LA BD REAL
    // ==========================================================
    private fun getMockCategories(): List<Category> {
        return listOf(
            Category(id = 1, name = "Bebidas", color = 0xFF2196F3.toInt(), sortOrder = 1),   // Azul
            Category(id = 2, name = "Botanas", color = 0xFFFF9800.toInt(), sortOrder = 2),   // Naranja
            Category(id = 3, name = "Limpieza", color = 0xFF4CAF50.toInt(), sortOrder = 3),  // Verde
            Category(id = 4, name = "Dulces", color = 0xFFE91E63.toInt(), sortOrder = 4)     // Rosa
        )
    }

    private fun getMockProducts(): List<Product> {
        return listOf(
            Product(id = 101, categoryId = 1, name = "Coca-Cola 600ml", price = 18.50, stock = 50.0, isVariablePrice = false, isActive = true),
            Product(id = 102, categoryId = 1, name = "Agua Natural Ciel 1L", price = 15.00, stock = 30.0, isVariablePrice = false, isActive = true),
            Product(id = 103, categoryId = 1, name = "Jugo del Valle Mango", price = 22.00, stock = 12.0, isVariablePrice = false, isActive = true),

            Product(id = 104, categoryId = 2, name = "Sabritas Sal 40g", price = 20.00, stock = 15.0, isVariablePrice = false, isActive = true),
            Product(id = 105, categoryId = 2, name = "Doritos Nacho 50g", price = 22.00, stock = 20.0, isVariablePrice = false, isActive = true),

            Product(id = 106, categoryId = 3, name = "Fabuloso Lavanda 1L", price = 35.00, stock = 10.0, isVariablePrice = false, isActive = true),
            Product(id = 107, categoryId = 3, name = "Jabón Zote Rosa", price = 24.00, stock = 45.0, isVariablePrice = false, isActive = true),

            // Producto con precio variable (a granel)
            Product(id = 108, categoryId = 4, name = "Gomitas a granel", price = 0.00, stock = 5.5, isVariablePrice = true, isActive = true),
            Product(id = 109, categoryId = 4, name = "Mazapán de la Rosa Gigante", price = 10.00, stock = 100.0, isVariablePrice = false, isActive = true)
        )
    }
}