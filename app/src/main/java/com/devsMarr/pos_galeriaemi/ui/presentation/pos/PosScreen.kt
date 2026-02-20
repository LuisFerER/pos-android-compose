package com.devsMarr.pos_galeriaemi.ui.presentation.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PosScreen(
    viewModel: PosViewModel = hiltViewModel()
) {
    // Subscripción al ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                // Header (WIP)
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    // TODO: Aquí irá la lista de items agregados
                    Text(
                        text = "Ticket",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Botón de cobrar (WIP)
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .fillMaxSize()
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                        .weight(0.15f)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "COBRAR",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            }
        }
    }
}