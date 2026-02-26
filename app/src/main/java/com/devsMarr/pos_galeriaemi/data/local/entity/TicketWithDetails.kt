package com.devsMarr.pos_galeriaemi.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

// Esta clase une automáticamente la Cabecera con sus Detalles
data class TicketWithDetails(
    @Embedded val ticketHead: TicketHeadEntity,

    @Relation(
        parentColumn = "id", // El ID del TicketHead
        entityColumn = "ticketId" // El ID foráneo en TicketDetail
    )
    val details: List<TicketDetailEntity>
)