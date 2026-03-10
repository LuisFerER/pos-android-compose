package com.devsMarr.pos_galeriaemi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cash_shifts")
data class CashShiftEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val openedById: Long, // ID del usuario que abrió la caja
    val closedById: Long? = null, // ID del usuario que cerró
    val status: String, // "OPEN" o "CLOSED"

    val startDate: Long = System.currentTimeMillis(), // Fecha de apertura (Timestamp)
    val endDate: Long? = null, // Fecha de cierre (Null = Turno Abierto/En curso)

    val startingCash: Double, // Fondo de caja inicial
    val finalCashCalculated: Double? = null, // Lo que el sistema dice que debe haber
    val finalCashReal: Double? = null, // Lo que el cajero contó físicamente al cerrar

    val difference: Double? = null, // finalCashReal - finalCashCalculated
    val notes: String? = null // Notas de diferencias
)