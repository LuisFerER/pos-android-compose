package com.devsMarr.pos_galeriaemi.ui.presentation.admin_panel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToDailyReport: () -> Unit,
    onNavigateToDateReports: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToEmployees: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onBackClick: () -> Unit
) {
    // Definimos la lista de opciones de navegación
    val menuOptions = listOf(
        AdminMenuOption("Reporte Diario", Icons.Default.PointOfSale, onNavigateToDailyReport),
        AdminMenuOption("Reportes por Fecha", Icons.Default.DateRange, onNavigateToDateReports),
        AdminMenuOption("Inventario", Icons.Default.Inventory, onNavigateToInventory),
        AdminMenuOption("Empleados", Icons.Default.People, onNavigateToEmployees),
        AdminMenuOption("Configuración", Icons.Default.Settings, onNavigateToSettings)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar al POS")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(menuOptions) { option ->
                AdminMenuCard(option = option)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminDashboardScreenPreview() {
    // Envolvemos en MaterialTheme para que agarre tus colores
    MaterialTheme {
        AdminDashboardScreen(
            onNavigateToDailyReport = {},
            onNavigateToDateReports = {},
            onNavigateToInventory = {},
            onNavigateToEmployees = {},
            onNavigateToSettings = {},
            onBackClick = {}
        )
    }
}