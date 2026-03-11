package com.devsMarr.pos_galeriaemi.ui.presentation.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsMarr.pos_galeriaemi.data.repository.UserRepository
import com.devsMarr.pos_galeriaemi.domain.model.User
import com.devsMarr.pos_galeriaemi.domain.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // 1. Exponemos la lista de usuarios activos para la pantalla principal
    // stateIn convierte el Flow del Repositorio en un StateFlow que Compose entiende perfectamente.
    val users: StateFlow<List<User>> = userRepository.getAllUsers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 2. Función para desactivar (Soft Delete)
    fun deactivateUser(user: User) {
        viewModelScope.launch {
            userRepository.deleteUser(user.id)
        }
    }

    // 3. Función para obtener un usuario específico (Útil para llenar el formulario al editar)
    suspend fun getUser(id: Long): User? {
        return userRepository.getUserById(id)
    }

    // 4. Lógica inteligente para guardar o actualizar
    fun saveUser(
        id: Long, // 0 si es nuevo, >0 si es edición
        firstName: String,
        lastName: String,
        phone: String,
        passwordInput: String,
        role: UserRole,
        onComplete: () -> Unit // Callback para avisarle a la UI que ya terminó y puede regresar
    ) {
        viewModelScope.launch {
            val isNewUser = id == 0L
            val isNewPassword = passwordInput.isNotBlank()

            // Si estamos editando y NO escribieron una nueva contraseña,
            // necesitamos mantener la contraseña encriptada anterior.
            val finalPassword = if (!isNewUser && !isNewPassword) {
                val existingUser = userRepository.getUserById(id)
                existingUser?.password ?: ""
            } else {
                passwordInput // El repositorio se encargará de encriptarla
            }

            val userToSave = User(
                id = id,
                firstName = firstName,
                lastName = lastName,
                phone = phone,
                password = finalPassword,
                role = role,
                isActive = true
            )

            // Le decimos al repo si debe encriptar (true) o si ya venía encriptada de antes (false)
            userRepository.saveUser(user = userToSave, isNewPassword = isNewPassword)

            // Avisamos a la pantalla que la operación fue un éxito
            onComplete()
        }
    }
}