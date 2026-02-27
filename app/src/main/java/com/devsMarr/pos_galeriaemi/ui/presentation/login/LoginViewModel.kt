package com.devsMarr.pos_galeriaemi.ui.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsMarr.pos_galeriaemi.data.repository.UserRepository
import com.devsMarr.pos_galeriaemi.domain.manager.SessionManager
import com.devsMarr.pos_galeriaemi.domain.model.User
import com.devsMarr.pos_galeriaemi.domain.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = true,
    val usersList: List<User> = emptyList(),
    val hasUsers: Boolean = false,

    // --- Para el Login normal ---
    val selectedUser: User? = null,
    val passwordInput: String = "",
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false,

    // --- Para crear el primer Admin ---
    val newFirstName: String = "",
    val newLastName: String = "",
    val newPhone: String = "",
    val newAdminPassword: String = ""
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        checkExistingUsers()
    }

    private fun checkExistingUsers() {
        viewModelScope.launch {
            // Escuchamos la lista de usuarios activos
            userRepository.getAllUsers().collect { users ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        usersList = users,
                        hasUsers = users.isNotEmpty(),
                        // Si hay usuarios, pre-seleccionamos al primero de la lista para ahorrarle un clic
                        selectedUser = if (users.isNotEmpty() && it.selectedUser == null) users.first() else it.selectedUser
                    )
                }
            }
        }
    }

    // --- FUNCIONES PARA EL LOGIN NORMAL ---
    fun onUserSelected(user: User) {
        _uiState.update { it.copy(selectedUser = user, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(passwordInput = password, errorMessage = null) }
    }

    fun login() {
        val currentState = _uiState.value
        if (currentState.selectedUser == null || currentState.passwordInput.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Selecciona un usuario y escribe tu contraseña") }
            return
        }

        viewModelScope.launch {
            // Buscamos si existe alguien con esa contraseña
            val userAttempt = userRepository.loginWithPassword(currentState.passwordInput)

            // Validamos que la contraseña exista Y que pertenezca al usuario seleccionado en el ComboBox
            if (userAttempt != null && userAttempt.id == currentState.selectedUser.id) {
                // ¡Éxito! Le ponemos su gafete virtual en el SessionManager
                sessionManager.login(userAttempt)
                _uiState.update { it.copy(loginSuccess = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(errorMessage = "Contraseña incorrecta") }
            }
        }
    }

    // --- FUNCIONES PARA EL PRIMER ADMIN ---
    fun onNewAdminDataChange(firstName: String, lastName: String, phone: String, pass: String) {
        _uiState.update { it.copy(newFirstName = firstName, newLastName = lastName, newPhone = phone, newAdminPassword = pass) }
    }

    fun createMasterAdmin() {
        val state = _uiState.value
        if (state.newFirstName.isBlank() || state.newAdminPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nombre y Contraseña son obligatorios") }
            return
        }

        viewModelScope.launch {
            val adminUser = User(
                firstName = state.newFirstName.trim(),
                lastName = state.newLastName.trim(),
                phone = state.newPhone.trim(),
                password = state.newAdminPassword.trim(),
                role = UserRole.ADMIN
            )
            // Lo guardamos indicando que es una contraseña nueva para que se encripte
            userRepository.saveUser(adminUser, isNewPassword = true)
            // Al guardarlo, el flow de checkExistingUsers() se actualizará solo y cambiará la pantalla a modo Login
        }
    }
}