package com.devsMarr.pos_galeriaemi.ui.presentation.category_form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormScreen(
    viewModel: CategoryFormViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit // Función para regresar a la pantalla anterior
) {
    val uiState by viewModel.uiState.collectAsState()

    // Efecto secundario: Escucha si la variable saveSuccess cambia a true
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onNavigateBack() // Cierra la pantalla automáticamente cuando se guarda
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.id == null) "Nueva Categoría" else "Editar Categoría",
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
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo de Nombre
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text("Nombre de la Categoría") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.name.isBlank() && uiState.isSaving // Pequeña validación visual
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Selector de Color
            Text("Color de la Categoría", style = MaterialTheme.typography.titleMedium)
            ColorPickerRow(
                selectedColor = uiState.color,
                onColorSelected = { viewModel.onColorChange(it) }
            )

            Spacer(modifier = Modifier.weight(1f)) // Empuja el botón hacia abajo

            // 4. Botón Guardar
            Button(
                onClick = { viewModel.saveCategory() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !uiState.isSaving && uiState.name.isNotBlank() // Se desactiva si está guardando o el nombre está vacío
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Guardar Categoría", fontSize = MaterialTheme.typography.titleMedium.fontSize)
                }
            }
        }
    }
}

// --- Componente Extra: Fila de Colores ---
@Composable
fun ColorPickerRow(
    selectedColor: Int,
    onColorSelected: (Int) -> Unit
) {
    // Una lista de colores bonitos predefinidos para Material Design
    val defaultColors = listOf(
        Color(0xFFF44336), // Rojo
        Color(0xFFE91E63), // Rosa
        Color(0xFF9C27B0), // Morado
        Color(0xFF3F51B5), // Indigo
        Color(0xFF2196F3), // Azul
        Color(0xFF00BCD4), // Cyan
        Color(0xFF4CAF50), // Verde
        Color(0xFFFF9800), // Naranja
        Color(0xFF795548), // Café
        Color(0xFF607D8B)  // Gris Azulado
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(defaultColors) { color ->
            val isSelected = color.toArgb() == selectedColor

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (isSelected) 3.dp else 0.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(color.toArgb()) },
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Color Seleccionado",
                        tint = Color.White
                    )
                }
            }
        }
    }
}