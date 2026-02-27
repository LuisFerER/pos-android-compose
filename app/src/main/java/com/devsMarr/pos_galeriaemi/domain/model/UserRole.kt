package com.devsMarr.pos_galeriaemi.domain.model

enum class UserRole {
    ADMIN,      // Tiene acceso a todo (Reportes, crear usuarios, etc)
    CASHIER     // Solo tiene acceso a la pantalla de cobro
}