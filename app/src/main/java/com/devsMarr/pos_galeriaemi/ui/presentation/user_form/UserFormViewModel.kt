package com.devsMarr.pos_galeriaemi.ui.presentation.user_form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsMarr.pos_galeriaemi.data.repository.UserRepository
import com.devsMarr.pos_galeriaemi.domain.model.User
import com.devsMarr.pos_galeriaemi.domain.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserFormViewModel @Inject constructor(
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserFormUiState())
    val uiState: StateFlow<UserFormUiState> = _uiState.asStateFlow()

    init {
        // Atrapamos el ID de la navegación tal como lo haces en productos
        val userId = savedStateHandle.get<Long>("userId")
        if (userId != null && userId != 0L) {
            loadUser(userId)
        }
    }

    private fun loadUser(id: Long) {
        viewModelScope.launch {
            val user = userRepository.getUserById(id)
            user?.let { u ->
                _uiState.update { state ->
                    state.copy(
                        id = u.id,
                        firstName = u.firstName,
                        lastName = u.lastName,
                        phone = u.phone,
                        role = u.role,
                        // IMPORTANTE: No cargamos la contraseña encriptada en la UI.
                        // Se deja en blanco para que el usuario escriba una nueva solo si quiere cambiarla.
                        password = ""
                    )
                }
            }
        }
    }

    // --- Funciones para que la UI actualice el estado ---
    fun onFirstNameChange(name: String) = _uiState.update { it.copy(firstName = name, errorMessage = null) }

    fun onLastNameChange(lastName: String) = _uiState.update { it.copy(lastName = lastName, errorMessage = null) }

    fun onPhoneChange(phone: String) {
        if (phone.isEmpty() || phone.all { it.isDigit() }) {
            _uiState.update { it.copy(phone = phone, errorMessage = null) }
        }
    }

    fun onPasswordChange(password: String) = _uiState.update { it.copy(password = password, errorMessage = null) }

    fun onRoleChange(role: UserRole) = _uiState.update { it.copy(role = role) }

    // --- Guardar en la Base de Datos ---
    fun saveUser() {
        val currentState = _uiState.value

        // Validaciones
        if (currentState.firstName.isBlank() || currentState.lastName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El nombre y apellido son obligatorios.") }
            return
        }

        // Si es un usuario nuevo, la contraseña es obligatoria
        val isNewUser = currentState.id == null || currentState.id == 0L
        if (isNewUser && currentState.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Debes asignar una contraseña al nuevo usuario.") }
            return
        }

        val isNewPassword = currentState.password.isNotBlank()

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            // Lógica para mantener la contraseña vieja si no se escribe una nueva al editar
            val finalPassword = if (!isNewUser && !isNewPassword) {
                val existingUser = userRepository.getUserById(currentState.id!!)
                existingUser?.password ?: ""
            } else {
                currentState.password
            }

            val userToSave = User(
                id = currentState.id ?: 0L,
                firstName = currentState.firstName.trim(),
                lastName = currentState.lastName.trim(),
                phone = currentState.phone.trim(),
                password = finalPassword,
                role = currentState.role,
                isActive = true
            )

            userRepository.saveUser(userToSave, isNewPassword)

            // Avisamos a la UI que fue un éxito
            _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }
}