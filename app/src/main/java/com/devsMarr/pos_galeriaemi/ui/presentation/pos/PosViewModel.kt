package com.devsMarr.pos_galeriaemi.ui.presentation.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsMarr.pos_galeriaemi.data.repository.CategoryRepository
import com.devsMarr.pos_galeriaemi.domain.model.Category
import com.devsMarr.pos_galeriaemi.domain.model.Product
import com.devsMarr.pos_galeriaemi.data.repository.ProductRepository
import com.devsMarr.pos_galeriaemi.data.repository.TicketRepository
import com.devsMarr.pos_galeriaemi.data.repository.CashShiftRepository
import com.devsMarr.pos_galeriaemi.data.repository.SettingsRepository
import com.devsMarr.pos_galeriaemi.domain.manager.SessionManager
import com.devsMarr.pos_galeriaemi.domain.service.PrinterService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PosViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val ticketRepository: TicketRepository,
    private val cashShiftRepository: CashShiftRepository,
    private val sessionManager: SessionManager,
    private val settingsRepository: SettingsRepository,
    private val printerService: PrinterService
) : ViewModel() {

    private val _uiState = MutableStateFlow(PosUiState())
    val uiState: StateFlow<PosUiState> = _uiState.asStateFlow()

    // Cache local
    private var fullProductList: List<Product> = emptyList()

    // Aquí guardaremos el ID de la caja para ponerlo en el Ticket al cobrar
    private var currentShiftId: Long? = null

    init {
        checkOpenShift() // Revisar si hay caja abierta al entrar
        subscribeToCatalog()
    }

    // --- LÓGICA DE TURNOS (CAJA) ---

    private fun checkOpenShift() {
        viewModelScope.launch {
            val currentShift = cashShiftRepository.getCurrentOpenShift()
            currentShiftId = currentShift?.id // Guardamos el ID en memoria

            _uiState.update {
                it.copy(
                    isShiftOpen = currentShift != null,
                    isCheckingShift = false
                )
            }
        }
    }

    fun openShift(initialAmount: Double) {
        viewModelScope.launch {
            val currentUser = sessionManager.getCurrentUser()
            if (currentUser == null) {
                _uiState.update { it.copy(shiftErrorMessage = "Error: Usuario no identificado") }
                return@launch
            }

            val result = cashShiftRepository.openShift(currentUser.id, initialAmount)

            if (result.isSuccess) {
                currentShiftId = result.getOrNull() // Guardamos el ID de la nueva caja
                _uiState.update { it.copy(isShiftOpen = true, shiftErrorMessage = null) }
            } else {
                _uiState.update { it.copy(shiftErrorMessage = result.exceptionOrNull()?.message) }
            }
        }
    }

    // --- ACCIONES DE CATÁLOGO ---

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
            currentCart.add(newItem)
        }

        val newTotal = currentCart.sumOf { it.totalLine }

        _uiState.value = _uiState.value.copy(
            cartItems = currentCart,
            totalAmount = newTotal
        )
    }

    fun addManualAmount(amount: Double, description: String = "Varios") {
        val currentCart = _uiState.value.cartItems.toMutableList()

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
            currentCart[itemIndex] = existingItem.copy(quantity = newQuantity)
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
            currentCart[itemIndex] = existingItem.copy(price = newPrice)
            val newTotal = currentCart.sumOf { it.totalLine }

            _uiState.value = _uiState.value.copy(
                cartItems = currentCart,
                totalAmount = newTotal
            )
        }
    }

    fun removeFromCart(cartItemId: Long) {
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

                val ticket = com.devsMarr.pos_galeriaemi.domain.model.Ticket(
                    shiftId = currentShiftId ?: 0L,
                    totalAmount = currentState.totalAmount,
                    receivedAmount = currentState.amountReceivedInput.toDoubleOrNull() ?: currentState.totalAmount,
                    changeAmount = currentState.changeDue,
                    paymentMethod = "EFECTIVO",
                    status = "COMPLETED",
                    details = domainDetails
                )

                val generatedTicketId = ticketRepository.saveSale(ticket)

                val savedTicket = ticket.copy(id = generatedTicketId)

                _uiState.value = _uiState.value.copy(
                    isSaleCompleted = true
                )

                launch(Dispatchers.IO) {
                    try {
                        val config = settingsRepository.appConfigFlow.first()

                        printerService.printTicket(savedTicket, config)

                    } catch (e: Exception) {
                        android.util.Log.e("POS_DEBUG", "Error al imprimir el ticket: ${e.message}")
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                android.util.Log.e("POS_DEBUG", "ERROR AL GUARDAR VENTA: ${e.message}")
            }
        }
    }

    fun acknowledgeSaleCompleted() {
        clearCart()
    }
}