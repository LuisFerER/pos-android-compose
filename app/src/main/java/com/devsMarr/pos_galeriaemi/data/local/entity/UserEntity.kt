package com.devsMarr.pos_galeriaemi.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    // Hacemos que la contraseña sea única para evitar duplicados accidentales
    indices = [Index(value = ["password"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val password: String, // Guardaremos el Hash, nunca el texto real
    val phone: String,
    val role: String, // Guardamos el Enum como String (Ej: "ADMIN" o "CASHIER")
    val isActive: Boolean = true
)