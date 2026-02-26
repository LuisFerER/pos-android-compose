package com.devsMarr.pos_galeriaemi.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.devsMarr.pos_galeriaemi.data.local.PosDatabase
import com.devsMarr.pos_galeriaemi.data.local.dao.TicketDetailDao
import com.devsMarr.pos_galeriaemi.data.local.dao.TicketHeadDao
import com.devsMarr.pos_galeriaemi.data.mapper.toEntity
import com.devsMarr.pos_galeriaemi.data.mapper.toHeadEntity
import com.devsMarr.pos_galeriaemi.domain.model.Ticket
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TicketRepository @Inject constructor(
    private val ticketHeadDao: TicketHeadDao,
    private val ticketDetailDao: TicketDetailDao,
    private val db: PosDatabase
) {

    suspend fun saveSale(ticket: Ticket): Long {

        return db.withTransaction {

            // 1. Convertimos y guardamos la cabecera
            val headEntity = ticket.toHeadEntity()
            val generatedTicketId = ticketHeadDao.insertTicketHead(headEntity)

            // Convertimos los detalles pasándoles el ID recién generado
            // y cambiando el ID de los productos a -1 si son Varios
            val detailEntities = ticket.details.map { detail ->
                val dbSafeProductId = if (detail.productId < 0) -1L else detail.productId

                val safeDetail = detail.copy(productId = dbSafeProductId)

                safeDetail.toEntity(newTicketId = generatedTicketId)
            }

            // Guardamos todos los detalles en la BD
            ticketDetailDao.insertDetails(detailEntities)

            // Retornamos el ID por si la UI lo necesita para imprimir el recibo
            generatedTicketId
        }
    }
}