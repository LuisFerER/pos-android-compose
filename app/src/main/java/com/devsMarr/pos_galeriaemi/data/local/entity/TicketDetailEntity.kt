package com.devsMarr.pos_galeriaemi.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ticket_details",
    foreignKeys = [
        ForeignKey(
            entity = TicketHeadEntity::class,
            parentColumns = ["id"],
            childColumns = ["ticketId"],
            onDelete = ForeignKey.CASCADE // Si se elimina el encabezado del ticket, se borran sus detalles automáticamente
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.RESTRICT // No permite borrar un producto de la BD si ya tiene ventas registradas
        )
    ],
    indices = [
        Index(value = ["ticketId"]),
        Index(value = ["productId"])
    ]
)
class TicketDetailEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val ticketId: Long, // Llave Foranea del ticket al que pertenece

    val productId: Long, // Llave Foranea del producto que esta en el ticket

    val productNameSnapshot: String, // Nombre del producto en el momento de la venta
    val unitPriceSnapshot: Double, // Precio del producto en el momento de la venta


    val quantity: Double, // Cantidad vendida

    val subtotal: Double // Resultado de quantity * unitPriceSnapshot

)