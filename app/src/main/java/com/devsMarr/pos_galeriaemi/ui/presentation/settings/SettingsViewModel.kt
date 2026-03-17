package com.devsMarr.pos_galeriaemi.ui.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsMarr.pos_galeriaemi.data.repository.SettingsRepository
import com.devsMarr.pos_galeriaemi.domain.model.AppConfig
import com.devsMarr.pos_galeriaemi.domain.manager.SessionManager
import com.devsMarr.pos_galeriaemi.domain.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // Recolectamos el primer valor que nos arroje el DataStore
            val config = settingsRepository.appConfigFlow.first()

            val currentUser = sessionManager.getCurrentUser()
            // Si por alguna razón es nulo, asumimos CAJERO por seguridad
            val role = currentUser?.role ?: UserRole.CASHIER
            _uiState.update { state ->
                state.copy(
                    businessName = config.businessName,
                    address = config.address,
                    phone = config.phone,
                    ticketFooter = config.ticketFooter,
                    printerMacAddress = config.printerMacAddress,
                    paperWidth = config.paperWidth,
                    isDarkMode = config.isDarkMode,
                    currentUserRole = role,
                    isLoading = false // Ya cargó
                )
            }
        }
    }

    // --- Funciones para actualizar el estado local ---
    fun onBusinessNameChange(value: String) = _uiState.update { it.copy(businessName = value) }
    fun onAddressChange(value: String) = _uiState.update { it.copy(address = value) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phone = value) }
    fun onTicketFooterChange(value: String) = _uiState.update { it.copy(ticketFooter = value) }
    fun onPrinterMacChange(value: String) = _uiState.update { it.copy(printerMacAddress = value) }
    fun onPaperWidthChange(value: Int) = _uiState.update { it.copy(paperWidth = value) }
    fun onDarkModeChange(value: Boolean) = _uiState.update { it.copy(isDarkMode = value) }

    // --- Guardar en DataStore ---
    fun saveSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val currentState = _uiState.value
            val newConfig = AppConfig(
                businessName = currentState.businessName.trim(),
                address = currentState.address.trim(),
                phone = currentState.phone.trim(),
                ticketFooter = currentState.ticketFooter.trim(),
                printerMacAddress = currentState.printerMacAddress.trim(),
                paperWidth = currentState.paperWidth,
                isDarkMode = currentState.isDarkMode
            )

            settingsRepository.updateConfig(newConfig)

            _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }
}