package com.devsMarr.pos_galeriaemi.ui.presentation.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.devsMarr.pos_galeriaemi.domain.model.Category
import com.devsMarr.pos_galeriaemi.domain.model.Product
import java.text.NumberFormat
import java.util.Locale

// CHIP DE CATEGORÍA ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterChip(
    category: Category?, // Si es null, representa "Todas"
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onSelected,
        label = {
            Text(text = category?.name ?: "Todas")
        },
        // Personalización de colores para que se vea como en la imagen (Verde seleccionado)
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = if (isSelected) Color.Transparent else Color.LightGray
        ),
        shape = MaterialTheme.shapes.large // Bordes redondeados
    )
}

// ---  TARJETA DE PRODUCTO ---
@Composable
fun ProductItemCard(
    product: Product,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }
    val stockColor = if (product.stock > 0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // (Opcional) Aquí iría la IMAGEN del producto si tuvieras una URL o recurso
            // Box(modifier = Modifier.size(60.dp).background(Color.LightGray))

            Spacer(modifier = Modifier.width(16.dp))

            // Info Central
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (product.stock > 0) "Stock: ${product.stock}" else "Agotado",
                    style = MaterialTheme.typography.bodySmall,
                    color = stockColor
                )
            }

            // Precio y Botones
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = currencyFormatter.format(product.price),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.Gray)
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}