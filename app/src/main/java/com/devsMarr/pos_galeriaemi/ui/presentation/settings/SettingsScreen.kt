package com.devsMarr.pos_galeriaemi.ui.presentation.settings

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
import com.devsMarr.pos_galeriaemi.domain.model.UserRole
import com.devsMarr.pos_galeriaemi.ui.presentation.settings.components.PaperWidthSelector
import com.devsMarr.pos_galeriaemi.ui.presentation.settings.components.SectionHeader
import com.devsMarr.pos_galeriaemi.ui.presentation.settings.components.SettingsSwitchRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración del Sistema", fontWeight = FontWeight.SemiBold) },
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
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                if (uiState.currentUserRole == UserRole.ADMIN) {
                    // --- DETALLES DEL NEGOCIO ---
                    SectionHeader("Detalles del Negocio")
                    OutlinedTextField(
                        value = uiState.businessName,
                        onValueChange = { viewModel.onBusinessNameChange(it) },
                        label = { Text("Nombre del Negocio") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = uiState.address,
                        onValueChange = { viewModel.onAddressChange(it) },
                        label = { Text("Dirección / Sucursal") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = uiState.phone,
                        onValueChange = { viewModel.onPhoneChange(it) },
                        label = { Text("Teléfono de Contacto") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // --- TICKET ---
                    SectionHeader("Impresión del Ticket")
                    OutlinedTextField(
                        value = uiState.ticketFooter,
                        onValueChange = { viewModel.onTicketFooterChange(it) },
                        label = { Text("Mensaje de pie de página (Agradecimiento/Políticas)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
                // --- HARDWARE ---
                SectionHeader("Hardware")
                OutlinedTextField(
                    value = uiState.printerMacAddress,
                    onValueChange = { viewModel.onPrinterMacChange(it) },
                    label = { Text("Dirección MAC Impresora Bluetooth (Ej. 00:11:22:33:44:55)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Text(
                    text = "Ancho del papel térmico",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
                PaperWidthSelector(
                    selectedWidth = uiState.paperWidth,
                    onWidthSelected = { viewModel.onPaperWidthChange(it) }
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // --- PREFERENCIAS ---
                SectionHeader("Preferencias de la App")
                SettingsSwitchRow(
                    title = "Modo Oscuro",
                    subtitle = "Forzar el tema oscuro en toda la aplicación",
                    isChecked = uiState.isDarkMode,
                    onCheckedChange = { viewModel.onDarkModeChange(it) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // --- BOTÓN GUARDAR ---
                Button(
                    onClick = { viewModel.saveSettings() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(bottom = 8.dp),
                    enabled = !uiState.isSaving
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Guardar Cambios", fontSize = MaterialTheme.typography.titleMedium.fontSize)
                    }
                }
            }
        }
    }
}