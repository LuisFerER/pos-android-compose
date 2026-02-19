package com.devsMarr.pos_galeriaemi.ui.presentation.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    // Inyectamos el ViewModel automáticamente
    viewModel: ProductListViewModel = hiltViewModel(),
    // Navegación (Callbacks)
    onNavigateToAddProduct: () -> Unit,
    onNavigateToEditProduct: (Long) -> Unit
) {
    // Observamos el ESTADO ÚNICO
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddProduct() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Producto")
            }
        }
    ) { innerPadding ->

        // Estructura Principal: Columna (Arriba: Categorías, Abajo: Productos)
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            // --- SECCIÓN A: CARRUSEL DE CATEGORÍAS (LazyRow) ---
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp), // Separación vertical
                contentPadding = PaddingValues(horizontal = 16.dp), // Margen a los lados
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre chips
            ) {
                // Chip "Todas" (Manual)
                item {
                    CategoryFilterChip(
                        category = null, // null representa "Todas"
                        isSelected = uiState.selectedCategoryId == null,
                        onSelected = { viewModel.onCategorySelected(null) }
                    )
                }

                // Chips Dinámicos (Desde la Base de Datos)
                items(items = uiState.categories, key = { it.id }) { category ->
                    CategoryFilterChip(
                        category = category,
                        isSelected = uiState.selectedCategoryId == category.id,
                        onSelected = { viewModel.onCategorySelected(category.id) }
                    )
                }
            }

            Divider(color = Color.LightGray.copy(alpha = 0.5f))

            // --- SECCIÓN B: LISTA DE PRODUCTOS (LazyColumn) ---
            if (uiState.isLoading) {
                // Cargando...
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.products.isEmpty()) {
                // Lista Vacía
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No hay productos en esta categoría",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                // Lista con Datos
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = uiState.products, key = { it.id }) { product ->
                        // Usamos el componente que creamos en el paso anterior
                        ProductItemCard(
                            product = product,
                            onEditClick = { onNavigateToEditProduct(product.id) },
                            onDeleteClick = { viewModel.onDeleteProduct(product.id) }
                        )
                    }
                }
            }
        }
    }
}