package com.devsMarr.pos_galeriaemi.data.repository

import androidx.room.withTransaction
import com.devsMarr.pos_galeriaemi.data.local.PosDatabase
import com.devsMarr.pos_galeriaemi.data.local.dao.ProductDao
import com.devsMarr.pos_galeriaemi.data.local.dao.TicketDetailDao
import com.devsMarr.pos_galeriaemi.data.local.dao.TicketHeadDao
import com.devsMarr.pos_galeriaemi.data.mapper.toDomain
import com.devsMarr.pos_galeriaemi.data.mapper.toEntity
import com.devsMarr.pos_galeriaemi.data.mapper.toHeadEntity
import com.devsMarr.pos_galeriaemi.domain.model.Ticket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TicketRepository @Inject constructor(
    private val ticketHeadDao: TicketHeadDao,
    private val ticketDetailDao: TicketDetailDao,
    private val productDao: ProductDao,
    private val db: PosDatabase
) {

    suspend fun saveSale(ticket: Ticket): Long {

        return db.withTransaction {

            // Convertimos y guardamos la cabecera
            val headEntity = ticket.toHeadEntity()
            val generatedTicketId = ticketHeadDao.insertTicketHead(headEntity)

            // Convertimos los detalles pasándoles el ID recién generado
            val detailEntities = ticket.details.map { detail ->
                detail.toEntity(newTicketId = generatedTicketId)
            }

            // Guardamos todos los detalles en la BD
            ticketDetailDao.insertDetails(detailEntities)

            // Restamos el inventario por cada producto vendido
            ticket.details.forEach { detail ->
                // Validamos que sea > 0L porque los "Montos Manuales" no existen en el catálogo
                if (detail.productId > 0L) {
                    productDao.decreaseStock(
                        productId = detail.productId,
                        quantity = detail.quantity
                    )
                }
            }

            // Retornamos el ID por si la UI lo necesita para imprimir el recibo
            generatedTicketId
        }
    }

    /**
     * Obtiene el historial completo de ventas de un turno.
     * Devuelve un Flow para que la pantalla se actualice sola si ocurre una nueva venta.
     */
    fun getTicketsHistory(shiftId: Long): Flow<List<Ticket>> {
        return ticketHeadDao.getFullTicketsByShift(shiftId).map { entitiesList ->
            entitiesList.map { ticketWithDetails ->
                ticketWithDetails.toDomain()
            }
        }
    }
}