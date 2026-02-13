package com.devsMarr.pos_galeriaemi.ui.presentation.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun PosScreen() {
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
            Box(
                modifier = Modifier
                    .weight(0.7f) // El 70% del ancho
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                // TODO: Aquí irá el Grid de Productos y Categorías
                Text(
                    text = "Área de Productos",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
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