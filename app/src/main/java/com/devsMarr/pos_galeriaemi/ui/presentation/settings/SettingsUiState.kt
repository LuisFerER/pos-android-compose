package com.devsMarr.pos_galeriaemi.ui.presentation.settings

data class SettingsUiState(
    // Datos del negocio
    val businessName: String = "",
    val address: String = "",
    val phone: String = "",
    // Ticket
    val ticketFooter: String = "",
    // Hardware
    val printerMacAddress: String = "",
    val paperWidth: Int = 58,
    // Preferencias
    val isDarkMode: Boolean = false,

    // Estados de la UI
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)