package com.devsMarr.pos_galeriaemi.domain.model

data class User(
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val password: String, // Aquí vivirá la contraseña ya encriptada
    val phone: String,
    val role: UserRole,
    val isActive: Boolean = true // Soft delete (para no borrar su historial si lo despiden)
) {
    // Helper para mostrar el nombre completo fácilmente en la UI
    val fullName: String
        get() = "$firstName $lastName".trim()
}