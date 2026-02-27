package com.devsMarr.pos_galeriaemi.data.mapper

import com.devsMarr.pos_galeriaemi.data.local.entity.UserEntity
import com.devsMarr.pos_galeriaemi.domain.model.User
import com.devsMarr.pos_galeriaemi.domain.model.UserRole

fun UserEntity.toDomain(): User {
    return User(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        password = this.password,
        phone = this.phone,
        // Convertimos el String de la BD a nuestro Enum de Kotlin
        role = try { UserRole.valueOf(this.role) } catch (e: Exception) { UserRole.CASHIER },
        isActive = this.isActive
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        password = this.password,
        phone = this.phone,
        role = this.role.name, // Convertimos el Enum a String ("ADMIN")
        isActive = this.isActive
    )
}