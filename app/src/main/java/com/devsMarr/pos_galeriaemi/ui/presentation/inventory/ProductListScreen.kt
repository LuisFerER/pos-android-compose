package com.devsMarr.pos_galeriaemi.ui.presentation.inventory

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel = hiltViewModel(),
    onNavigateToAddProduct: () -> Unit,
    onNavigateToEditProduct: (Long) -> Unit,
    onNavigateToAddCategory: () -> Unit,
    onNavigateToEditCategory: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // --- Estados para controlar los diálogos de confirmación ---
    var categoryToDelete by remember { mutableStateOf<Long?>(null) }
    var productToDelete by remember { mutableStateOf<Long?>(null) }

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

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // --- SECCIÓN A: CARRUSEL DE CATEGORÍAS ---
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Botón "+ Nueva"
                item {
                    AssistChip(
                        onClick = onNavigateToAddCategory,
                        label = { Text("Nueva", fontWeight = FontWeight.Bold) },
                        leadingIcon = {
                            Icon(Icons.Default.Add, contentDescription = "Agregar Categoría", modifier = Modifier.size(AssistChipDefaults.IconSize))
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            leadingIconContentColor = MaterialTheme.colorScheme.primary,
                            labelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                // 2. Botón "Todas"
                item {
                    FilterChip(
                        selected = uiState.selectedCategoryId == null,
                        onClick = { viewModel.onCategorySelected(null) },
                        label = { Text("Todas") }
                    )
                }

                // 3. Categorías Dinámicas
                items(items = uiState.categories, key = { it.id }) { category ->
                    var showMenu by remember { mutableStateOf(false) }
                    val isSelected = uiState.selectedCategoryId == category.id

                    Box {
                        Surface(
                            modifier = Modifier.combinedClickable(
                                onClick = { viewModel.onCategorySelected(category.id) },
                                onLongClick = { showMenu = true }
                            ),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = category.name,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Editar") },
                                onClick = {
                                    showMenu = false
                                    onNavigateToEditCategory(category.id)
                                },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = "Editar") }
                            )
                            DropdownMenuItem(
                                text = { Text("Eliminar", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showMenu = false
                                    // NUEVO: En lugar de borrar directo, guardamos el ID para mostrar el diálogo
                                    categoryToDelete = category.id
                                },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error) }
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            // --- SECCIÓN B: LISTA DE PRODUCTOS ---
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay productos en esta categoría", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = uiState.products, key = { it.id }) { product ->
                        ProductItemCard(
                            product = product,
                            onEditClick = { onNavigateToEditProduct(product.id) },
                            // NUEVO: En lugar de borrar directo, guardamos el ID para mostrar el diálogo
                            onDeleteClick = { productToDelete = product.id }
                        )
                    }
                }
            }
        }

        // =======================================================================
        // ZONA DE DIÁLOGOS EMERGENTES
        // =======================================================================

        // 1. Confirmación para eliminar CATEGORÍA
        if (categoryToDelete != null) {
            AlertDialog(
                onDismissRequest = { categoryToDelete = null },
                icon = { Icon(Icons.Default.Warning, contentDescription = "Advertencia", tint = MaterialTheme.colorScheme.error) },
                title = { Text("Eliminar Categoría") },
                text = { Text("¿Estás seguro de que deseas eliminar esta categoría? Si tiene productos asignados, la acción será cancelada por seguridad.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteCategory(categoryToDelete!!)
                            categoryToDelete = null // Cerramos el diálogo
                        }
                    ) {
                        Text("Sí, Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { categoryToDelete = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // 2. Confirmación para eliminar PRODUCTO
        if (productToDelete != null) {
            AlertDialog(
                onDismissRequest = { productToDelete = null },
                icon = { Icon(Icons.Default.Warning, contentDescription = "Advertencia", tint = MaterialTheme.colorScheme.error) },
                title = { Text("Eliminar Producto") },
                text = { Text("¿Estás seguro de que deseas eliminar este producto?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.onDeleteProduct(productToDelete!!)
                            productToDelete = null // Cerramos el diálogo
                        }
                    ) {
                        Text("Sí, Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { productToDelete = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // 3. Alerta de ERROR (Viene desde el ViewModel si la base de datos bloqueó la eliminación)
        if (errorMessage != null) {
            AlertDialog(
                onDismissRequest = { viewModel.clearErrorMessage() },
                icon = { Icon(Icons.Default.Warning, contentDescription = "Advertencia", tint = MaterialTheme.colorScheme.error) },
                title = { Text("Acción Denegada") },
                text = { Text(errorMessage!!) },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearErrorMessage() }) {
                        Text("Entendido")
                    }
                }
            )
        }
    }
}