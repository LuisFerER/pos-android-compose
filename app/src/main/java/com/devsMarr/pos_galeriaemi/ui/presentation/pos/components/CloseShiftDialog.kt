package com.devsMarr.pos_galeriaemi.ui.presentation.pos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun CloseShiftDialog(
    startingCash: Double, // <-- NUEVO: Fondo inicial
    totalSales: Double,   // <-- NUEVO: Ventas del turno
    expectedAmount: Double,
    onConfirm: (actualAmount: Double, notes: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var actualAmountInput by remember { mutableStateOf("") }
    var notesInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Corte de Caja", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- DESGLOSE DEL CORTE ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Resumen del Turno",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Fondo Inicial:", style = MaterialTheme.typography.bodyMedium)
                        Text("$${String.format("%.2f", startingCash)}", style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("+ Ventas Efectivo:", style = MaterialTheme.typography.bodyMedium)
                        Text("$${String.format("%.2f", totalSales)}", style = MaterialTheme.typography.bodyMedium)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("= TOTAL ESPERADO:", fontWeight = FontWeight.Bold)
                        Text(
                            text = "$${String.format("%.2f", expectedAmount)}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                // --- FIN DEL DESGLOSE ---

                OutlinedTextField(
                    value = actualAmountInput,
                    onValueChange = {
                        actualAmountInput = it
                        isError = false
                    },
                    label = { Text("¿Cuánto dinero cuentas en físico?") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = isError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (isError) {
                    Text(
                        text = "Ingresa una cantidad válida (Ej: 1500.50).",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("Notas de sobrante/faltante (Opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val actual = actualAmountInput.toDoubleOrNull()
                    if (actual != null && actual >= 0.0) {
                        onConfirm(actual, notesInput.takeIf { it.isNotBlank() })
                    } else {
                        isError = true
                    }
                }
            ) {
                Text("Cerrar Turno")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}