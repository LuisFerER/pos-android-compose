package com.devsMarr.pos_galeriaemi.ui.presentation.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devsMarr.pos_galeriaemi.domain.model.Product

@Composable
fun PosScreen(
    viewModel: PosViewModel = hiltViewModel()
) {
    // Subscripción al ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Estado para mostrar/ocultar el diálogo de cobro
    var showCheckoutDialog by remember { mutableStateOf(false) }
    var showVariosDialog by remember { mutableStateOf(false) }

    // Observador reactivo: Cuando el ViewModel diga que la venta se completó,
    // cerramos el diálogo y reseteamos el POS para el siguiente cliente.
    LaunchedEffect(uiState.isSaleCompleted) {
        if (uiState.isSaleCompleted) {
            showCheckoutDialog = false
            viewModel.acknowledgeSaleCompleted()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->

        // Contenedor Principal
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // ---------------------------------------------------------
            // SECCIÓN IZQUIERDA: PRODUCTOS
            // ---------------------------------------------------------
            Column(
                modifier = Modifier
                    .weight(0.7f) // El 70% del ancho
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.background),
            ) {
                PosSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { newText ->
                        viewModel.searchProduct(newText)
                    },
                    modifier = Modifier.padding(16.dp)
                )

                CategoryCarousel(
                    categories = uiState.categories,
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = { category ->
                        viewModel.filterByCategory(category)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (uiState.isLoadingCatalog) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    ProductGrid(
                        products = uiState.productsCatalog,
                        onProductClick = { product ->
                            viewModel.addToCart(product)
                        },
                        onVariosClick = {
                            showVariosDialog = true
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Separador
            VerticalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 1.dp
            )

            // ---------------------------------------------------------
            // SECCIÓN DERECHA: TICKET
            // ---------------------------------------------------------
            Column(
                modifier = Modifier
                    .weight(0.3f) // El ancho restante
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface), // Color Ticket
                verticalArrangement = Arrangement.SpaceBetween, // Para empujar el total abajo
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header del Ticket
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Orden Actual",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                // Lista de Productos en el Carrito
                if (uiState.cartItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "El carrito está vacío",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(uiState.cartItems) { cartItem ->
                            CartItemRow(
                                item = cartItem,
                                onIncreaseQuantity = {
                                    viewModel.updateQuantity(cartItem.id, cartItem.quantity + 1.0)
                                },
                                onDecreaseQuantity = {
                                    viewModel.updateQuantity(cartItem.id, cartItem.quantity - 1.0)
                                },
                                onManualQuantityChange = { newQuantity ->
                                    viewModel.updateQuantity(cartItem.id, newQuantity)
                                },
                                onRemove = {
                                    viewModel.removeFromCart(cartItem.id)
                                }
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .padding(16.dp)
                ) {
                    // Total Fila
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$ ${String.format("%.2f", uiState.totalAmount)}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Botón COBRAR
                    Button(
                        onClick = {
                            showCheckoutDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = uiState.cartItems.isNotEmpty()
                    ) {
                        Text(
                            text = "COBRAR",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (showCheckoutDialog) {
            CheckoutDialog(
                totalAmount = uiState.totalAmount,
                amountReceived = uiState.amountReceivedInput,
                changeDue = uiState.changeDue,
                isPaymentSufficient = uiState.isPaymentSufficient,
                onAmountReceivedChange = { viewModel.onAmountReceivedChange(it) },
                onConfirmSale = { viewModel.finalizeSale() },
                onDismiss = {
                    showCheckoutDialog = false
                    viewModel.onAmountReceivedChange("") // Limpiamos el input si cancela
                }
            )
        }

        if (showVariosDialog) {
            VariosPriceDialog(
                onDismiss = { showVariosDialog = false },
                onConfirm = { enteredName, enteredPrice ->

                    val variosProduct = Product(
                        id = -(System.currentTimeMillis() % 10000),
                        categoryId = 0,
                        name = enteredName,
                        price = enteredPrice,
                        stock = 999.0,
                        isVariablePrice = true,
                        isActive = true
                    )

                    viewModel.addToCart(variosProduct)
                    showVariosDialog = false
                }
            )
        }
    }
}