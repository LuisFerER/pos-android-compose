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

// (Opcional por ahora) Entity -> Domain: Útil para cuando hagas la pantalla de "Historial de Ventas"
// fun TicketHeadEntity.toDomain(details: List<TicketDetailEntity>): Ticket { ... }