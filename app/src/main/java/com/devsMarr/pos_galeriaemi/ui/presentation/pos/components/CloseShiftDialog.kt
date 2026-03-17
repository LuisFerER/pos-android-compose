package com.devsMarr.pos_galeriaemi.ui.presentation.pos.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun CloseShiftDialog(
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
                Text(
                    text = "Dinero calculado por el sistema: $${String.format("%.2f", expectedAmount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

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