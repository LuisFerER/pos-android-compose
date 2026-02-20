package com.devsMarr.pos_galeriaemi.ui.presentation.product_form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    viewModel: ProductFormViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit // Función para regresar
) {
    val uiState by viewModel.uiState.collectAsState()

    // Observamos si se guardó con éxito para cerrar la pantalla
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.id == null) "Nuevo Producto" else "Editar Producto",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), // Permite hacer scroll si el teclado tapa los campos
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- MENSAJE DE ERROR ---
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // --- NOMBRE ---
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text("Nombre del Producto *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // --- CATEGORÍA (Menú Desplegable) ---
            var expanded by remember { mutableStateOf(false) } // Controla si el menú está abierto
            val selectedCategoryName = uiState.categories.find { it.id == uiState.categoryId }?.name ?: "Selecciona una categoría *"

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCategoryName,
                    onValueChange = {},
                    readOnly = true, // Evita que el usuario escriba, solo puede seleccionar
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (uiState.categories.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No hay categorías creadas") },
                            onClick = { expanded = false }
                        )
                    } else {
                        uiState.categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    viewModel.onCategoryChange(category.id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // --- PRECIO Y STOCK (En una misma fila para ahorrar espacio) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.price,
                    onValueChange = { viewModel.onPriceChange(it) },
                    label = { Text("Precio *") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    prefix = { Text("$") }
                )

                OutlinedTextField(
                    value = uiState.stock,
                    onValueChange = { viewModel.onStockChange(it) },
                    label = { Text("Stock inicial") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }

            // --- PRECIO VARIABLE (Switch) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Precio Variable", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Permite modificar el precio al momento de cobrar",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Switch(
                    checked = uiState.isVariablePrice,
                    onCheckedChange = { viewModel.onVariablePriceChange(it) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- BOTÓN GUARDAR ---
            Button(
                onClick = { viewModel.saveProduct() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Guardar Producto", fontSize = MaterialTheme.typography.titleMedium.fontSize)
                }
            }
        }
    }
}