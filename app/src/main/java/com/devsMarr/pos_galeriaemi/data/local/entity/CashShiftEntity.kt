package com.devsMarr.pos_galeriaemi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cash_shifts")
data class CashShiftEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val startDate: Long = System.currentTimeMillis(), // Fecha de apertura (Timestamp)

    val endDate: Long? = null, // Fecha de cierre (Null = Turno Abierto/En curso)

    val startingCash: Double, // Fondo de caja inicial

    val finalCashCalculated: Double = 0.0, // Lo que el sistema dice que debe haber

    val finalCashReal: Double = 0.0, // Lo que el cajero contó físicamente al cerrar

    val notes: String? = null // Notas de diferencias
)