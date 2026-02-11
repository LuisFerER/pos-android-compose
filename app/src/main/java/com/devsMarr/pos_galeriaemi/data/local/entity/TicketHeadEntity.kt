package com.devsMarr.pos_galeriaemi.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ticket_heads",
    // Relación: Una venta PERTENECE a un Turno de Caja
    foreignKeys = [
        ForeignKey(
            entity = CashShiftEntity::class,
            parentColumns = ["id"],
            childColumns = ["shiftId"],
            onDelete = ForeignKey.RESTRICT // No se permite borrar un turno si ya tiene ventas registradas
        )
    ],

    indices = [Index(value = ["shiftId"])]
)
data class TicketHeadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val shiftId: Long, // Llave Foranea del turno actual

    val timestamp: Long = System.currentTimeMillis(), // Fecha y hora de la venta

    val totalAmount: Double, // Importe total de la venta

    val receivedAmount: Double, // Importe recibido del cliente

    val changeAmount: Double, // Cambio entregado

    val paymentMethod: String = "CASH", // Metodo de pago

    val status: String = "COMPLETED" // COMPLETED o CANCELLED
)