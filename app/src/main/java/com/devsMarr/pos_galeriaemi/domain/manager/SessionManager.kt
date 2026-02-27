package com.devsMarr.pos_galeriaemi.domain.manager

import com.devsMarr.pos_galeriaemi.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {

    // Guarda al usuario actual. Si es null, significa que nadie ha iniciado sesión.
    private val _currentUser = MutableStateFlow<User?>(null)

    // Lo exponemos como StateFlow para que las pantallas reaccionen automáticamente
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun login(user: User) {
        _currentUser.value = user
    }

    fun logout() {
        _currentUser.value = null
    }

    // Función rápida para obtener el usuario sin tener que observar el Flow
    fun getCurrentUser(): User? {
        return _currentUser.value
    }
}