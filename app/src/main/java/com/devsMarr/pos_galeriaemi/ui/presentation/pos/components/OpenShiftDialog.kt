package com.devsMarr.pos_galeriaemi.ui.presentation.pos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun OpenShiftDialog(
    onConfirm: (Double) -> Unit,
    onLogoutClick: () -> Unit // Por si el cajero se arrepiente y quiere regresar al Login
) {
    // Estado para guardar lo que escribe el usuario
    var inputAmount by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        // Al dejar esto vacío, evitamos que el cajero cierre el cuadro tocando fuera de él
        onDismissRequest = { },
        title = {
            Text(text = "Abrir Caja", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Para comenzar a cobrar, ingresa el fondo de caja inicial (monedas y billetes) con el que empiezas tu turno.",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = inputAmount,
                    onValueChange = {
                        // Permitimos escribir, pero limpiamos el error si empieza a corregir
                        inputAmount = it
                        isError = false
                    },
                    label = { Text("Fondo inicial ($)") },
                    // Mostramos el teclado numérico con punto decimal
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = isError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (isError) {
                    Text(
                        text = "Por favor, ingresa una cantidad válida (Ej: 500 o 500.50).",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Validamos que sea un número real y que no sea negativo
                    val amount = inputAmount.toDoubleOrNull()
                    if (amount != null && amount >= 0.0) {
                        onConfirm(amount)
                    } else {
                        isError = true
                    }
                }
            ) {
                Text("Iniciar Turno")
            }
        },
        dismissButton = {
            TextButton(onClick = onLogoutClick) {
                Text("Salir", color = MaterialTheme.colorScheme.error)
            }
        }
    )
}