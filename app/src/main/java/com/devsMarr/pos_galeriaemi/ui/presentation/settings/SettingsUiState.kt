package com.devsMarr.pos_galeriaemi.ui.presentation.settings

import com.devsMarr.pos_galeriaemi.domain.model.UserRole

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
    // Rol del usuario
    val currentUserRole: UserRole = UserRole.CASHIER,

    // Estados de la UI
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)