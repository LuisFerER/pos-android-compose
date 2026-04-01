package com.devsMarr.pos_galeriaemi.ui.presentation.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devsMarr.pos_galeriaemi.domain.model.Ticket
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.Intent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class) // <-- Agregamos ExperimentalFoundationApi
@Composable
fun TicketHistoryScreen(
    viewModel: TicketHistoryViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val exportedFile by viewModel.exportedFile.collectAsState()
    val context = LocalContext.current

    var showDatePicker by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = uiState.startDateMillis,
        initialSelectedEndDateMillis = uiState.endDateMillis
    )

    val displayFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val startString = displayFormatter.format(Date(uiState.startDateMillis))
    val endString = displayFormatter.format(Date(uiState.endDateMillis))

    val selectedDateText = if (startString == endString) startString else "$startString - $endString"

    // Agrupamos los tickets por fecha
    val groupedTickets = remember(uiState.tickets) {
        uiState.tickets.groupBy { ticket ->
            val format = SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("es", "MX"))
            format.format(Date(ticket.timestamp)).replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        }
    }

    LaunchedEffect(exportedFile) {
        exportedFile?.let { file ->
            try {
                // Usa el FileProvider para crear una URI segura
                val authority = "${context.packageName}.provider"
                val uri = FileProvider.getUriForFile(context, authority, file)

                // Crea el Intent para compartir
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv" // Le dice a Android que es un archivo tipo Excel/CSV
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "Historial de Ventas")
                    // Le da permiso a las otras apps para leer esta URI temporal
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                // Abre el menú nativo de Android
                context.startActivity(Intent.createChooser(shareIntent, "Exportar a Excel usando:"))

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                // Limpia el estado para no repetir la acción
                viewModel.clearExportedFile()
            }
        }
    }
    var isExportMenuExpanded by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Historial de Ventas", fontWeight = FontWeight.Bold)
                        Text(
                            text = selectedDateText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Filtrar por Fecha")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            // Un Row para alinear los botones horizontalmente (de izquierda a derecha)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                // --- BOTONES SECUNDARIOS (PDF y EXCEL) ---
                AnimatedVisibility(
                    visible = isExportMenuExpanded,
                    enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }), // Entran desde la derecha
                    exit = fadeOut() + slideOutHorizontally(targetOffsetX = { it })  // Salen hacia la derecha
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(end = 16.dp) // Espacio entre estos botones y el principal
                    ) {
                        // 1. Botón de PDF (Para el Dev 1)
                        ExtendedFloatingActionButton(
                            onClick = {
                                isExportMenuExpanded = false
                                // TODO: Llamar a la función del PDF cuando el Dev 1 la termine
                            },
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            icon = { Icon(Icons.Default.Description, contentDescription = "PDF") },
                            text = { Text("PDF") }
                        )

                        // 2. Botón de Excel (Tu función de la Tarea 2.4)
                        ExtendedFloatingActionButton(
                            onClick = {
                                isExportMenuExpanded = false
                                viewModel.exportCurrentHistory() // Llamamos a tu función
                            },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            icon = { Icon(Icons.Default.List, contentDescription = "Excel") }, // Usa TableChart si tienes los iconos extendidos
                            text = { Text("Excel") }
                        )
                    }
                }

                // --- BOTÓN PRINCIPAL (El que abre/cierra el menú) ---
                FloatingActionButton(
                    onClick = { isExportMenuExpanded = !isExportMenuExpanded },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    // Cambiamos el icono a una 'X' si está abierto
                    Icon(
                        imageVector = if (isExportMenuExpanded) Icons.Default.Close else Icons.Default.Share,
                        contentDescription = if (isExportMenuExpanded) "Cerrar menú" else "Exportar"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.tickets.isEmpty()) {
                Text(
                    text = "No hay ventas en: $selectedDateText",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Iteramos sobre los grupos de fechas
                    groupedTickets.forEach { (dateString, ticketsForDate) ->

                        // Dibujamos el encabezado de la fecha
                        stickyHeader {
                            Surface(
                                color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f), // Ligeramente transparente
                                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp, top = 8.dp)
                            ) {
                                Text(
                                    text = dateString,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }

                        // Dibujamos los tickets que pertenecen a ese día
                        items(ticketsForDate, key = { it.id }) { ticket ->
                            TicketCard(ticket = ticket)
                        }
                    }
                }
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                            viewModel.updateSelectedDateRange(
                                startUtcMillis = dateRangePickerState.selectedStartDateMillis,
                                endUtcMillis = dateRangePickerState.selectedEndDateMillis
                            )
                        }
                    ) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancelar")
                    }
                }
            ) {
                DateRangePicker(
                    state = dateRangePickerState,
                    modifier = Modifier.weight(1f),
                    title = {
                        Text("Seleccionar Rango", modifier = Modifier.padding(16.dp))
                    }
                )
            }
        }
    }
}

@Composable
fun TicketCard(ticket: Ticket) {
    // Variable para saber si la tarjeta está abierta o cerrada
    var expanded by remember { mutableStateOf(false) }

    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val timeString = timeFormatter.format(Date(ticket.timestamp))
    val totalItems = ticket.details.sumOf { it.quantity }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            // Hacemos que toda la tarjeta sea "clickeable"
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // --- PARTE SUPERIOR (Siempre visible) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = "Ticket",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Folio: #${ticket.id}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$timeString • $totalItems artículos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$ ${String.format("%.2f", ticket.totalAmount)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    // Icono de flechita que cambia si está abierto o cerrado
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Desplegar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // --- PARTE INFERIOR (Se despliega al hacer clic) ---
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))

                    Text(
                        text = "Detalle de la venta:",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Imprimimos cada producto que se vendió
                    ticket.details.forEach { detail ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                // Formato: "2.0x Coca Cola"
                                text = "${detail.quantity}x ${detail.productNameSnapshot}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "$${String.format("%.2f", detail.subtotal)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Extra: Mostrar con cuánto pagó y su cambio
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Recibido:", style = MaterialTheme.typography.bodySmall)
                        Text("$${String.format("%.2f", ticket.receivedAmount)}", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Cambio dado:", style = MaterialTheme.typography.bodySmall)
                        Text("$${String.format("%.2f", ticket.changeAmount)}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}