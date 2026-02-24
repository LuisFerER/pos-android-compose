package com.devsMarr.pos_galeriaemi.domain.model

// El detalle individual
data class TicketDetail(
    val id: Long = 0,
    val ticketId: Long = 0, // Se llenará al guardar
    val productId: Long,
    val productNameSnapshot: String,
    val unitPriceSnapshot: Double,
    val quantity: Double,
    val subtotal: Double
)

// La cabecera que CONTIENE a sus detalles
data class Ticket(
    val id: Long = 0,
    val shiftId: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val totalAmount: Double,
    val receivedAmount: Double,
    val changeAmount: Double,
    val paymentMethod: String = "CASH",
    val status: String = "COMPLETED",
    val details: List<TicketDetail> // <-- Aquí está la magia del dominio, todo agrupado
)