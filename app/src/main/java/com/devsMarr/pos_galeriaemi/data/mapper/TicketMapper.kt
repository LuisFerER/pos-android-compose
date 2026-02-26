package com.devsMarr.pos_galeriaemi.data.mapper

import com.devsMarr.pos_galeriaemi.data.local.entity.TicketDetailEntity
import com.devsMarr.pos_galeriaemi.data.local.entity.TicketHeadEntity
import com.devsMarr.pos_galeriaemi.domain.model.Ticket
import com.devsMarr.pos_galeriaemi.domain.model.TicketDetail

// Convierte la cabecera de Dominio a Entity de BD
fun Ticket.toHeadEntity(): TicketHeadEntity {
    return TicketHeadEntity(
        id = this.id,
        shiftId = this.shiftId,
        timestamp = this.timestamp,
        totalAmount = this.totalAmount,
        receivedAmount = this.receivedAmount,
        changeAmount = this.changeAmount,
        paymentMethod = this.paymentMethod,
        status = this.status
    )
}

// Convierte un detalle de Dominio a Entity de BD, inyectándole el ID del padre
fun TicketDetail.toEntity(newTicketId: Long): TicketDetailEntity {
    return TicketDetailEntity(
        id = this.id,
        ticketId = newTicketId, // Le asignamos el ID del ticket recién creado
        productId = this.productId,
        productNameSnapshot = this.productNameSnapshot,
        unitPriceSnapshot = this.unitPriceSnapshot,
        quantity = this.quantity,
        subtotal = this.subtotal
    )
}

// Convierte un Entity de Detalle al de Dominio
fun TicketDetailEntity.toDomain(): TicketDetail {
    return TicketDetail(
        id = this.id,
        ticketId = this.ticketId,
        productId = this.productId,
        productNameSnapshot = this.productNameSnapshot,
        unitPriceSnapshot = this.unitPriceSnapshot,
        quantity = this.quantity,
        subtotal = this.subtotal
    )
}

// Convierte la Relación completa de Room a tu Ticket de Dominio
fun com.devsMarr.pos_galeriaemi.data.local.entity.TicketWithDetails.toDomain(): Ticket {
    return Ticket(
        id = this.ticketHead.id,
        shiftId = this.ticketHead.shiftId,
        timestamp = this.ticketHead.timestamp,
        totalAmount = this.ticketHead.totalAmount,
        receivedAmount = this.ticketHead.receivedAmount,
        changeAmount = this.ticketHead.changeAmount,
        paymentMethod = this.ticketHead.paymentMethod,
        status = this.ticketHead.status,
        // Mapeamos la lista de detalles
        details = this.details.map { it.toDomain() }
    )
}