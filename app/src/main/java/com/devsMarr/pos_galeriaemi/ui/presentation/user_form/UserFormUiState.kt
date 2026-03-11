package com.devsMarr.pos_galeriaemi.ui.presentation.user_form

import com.devsMarr.pos_galeriaemi.domain.model.UserRole

data class UserFormUiState(
    val id: Long? = null, // null = Crear nuevo, con número = Editar
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val password: String = "", // Vacío por defecto (al editar, si se queda vacío no se cambia)
    val role: UserRole = UserRole.CASHIER,

    // Estados de la UI
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)