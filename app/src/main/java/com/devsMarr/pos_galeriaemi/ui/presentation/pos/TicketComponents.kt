package com.devsMarr.pos_galeriaemi.ui.presentation.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay


@Composable
fun CartItemRow(
    item: CartItem,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
    onManualQuantityChange: (Double) -> Unit, // <-- NUEVO EVENTO
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estado para controlar si mostramos o no el popup
    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        EditQuantityDialog(
            initialQuantity = item.quantity,
            onDismiss = { showEditDialog = false },
            onConfirm = { newQty ->
                onManualQuantityChange(newQty)
                showEditDialog = false
            }
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Fila 1 Nombre y Botón Borrar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {

                // Nombre del producto
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Botón borrar
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fila 2 Controles de Cantidad y Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
                ) {

                    // Botón para restar
                    IconButton(onClick = onDecreaseQuantity, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Remove, contentDescription = "Restar", modifier = Modifier.size(16.dp))
                    }

                    // Texto clickleable para mostrar la cantidad
                    Text(
                        text = if (item.quantity % 1.0 == 0.0) item.quantity.toInt().toString() else item.quantity.toString(),
                        modifier = Modifier
                            .clickable { showEditDialog = true } // ¡Abre el diálogo!
                            .padding(horizontal = 16.dp, vertical = 8.dp), // Área táctil más grande
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary // Le damos color para indicar que es interactivo
                    )

                    // Botón para sumar
                    IconButton(onClick = onIncreaseQuantity, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Sumar", modifier = Modifier.size(16.dp))
                    }
                }

                // Total de la linea
                Text(
                    text = "$ ${String.format("%.2f", item.totalLine)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Composable
fun EditQuantityDialog(
    initialQuantity: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    val initialString = if (initialQuantity % 1.0 == 0.0) initialQuantity.toInt().toString() else initialQuantity.toString()

    // Seleccionar texto al abrir el diálogo
    var inputText by remember {
        mutableStateOf(
            TextFieldValue(
                text = initialString,
                selection = TextRange(0, initialString.length) // Selección del inicio al fin
            )
        )
    }

    // Pedir el focus al TextField
    val focusRequester = remember { FocusRequester() }

    // Efecto al lanzar
    LaunchedEffect(Unit) {
        delay(100) // Retraso para permitir el dibujar el dialog
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Cantidad") },
        text = {
            OutlinedTextField(
                value = inputText,
                onValueChange = { newValue ->
                    val text = newValue.text
                    if (text.isEmpty() || text.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        inputText = newValue
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val newQty = inputText.text.toDoubleOrNull()
                    if (newQty != null && newQty > 0) {
                        onConfirm(newQty)
                    } else if (newQty == 0.0) {
                        onConfirm(0.0)
                    }
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}