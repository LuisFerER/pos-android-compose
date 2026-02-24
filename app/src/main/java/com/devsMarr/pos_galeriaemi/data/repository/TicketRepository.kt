package com.devsMarr.pos_galeriaemi.data.repository

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

            // 2. Convertimos los detalles pasándoles el ID recién generado
            val detailEntities = ticket.details.map { detail ->
                detail.toEntity(newTicketId = generatedTicketId)
            }

            // 3. Guardamos todos los detalles en la BD
            ticketDetailDao.insertDetails(detailEntities)

            // Retornamos el ID por si la UI lo necesita para imprimir el recibo
            generatedTicketId
        }
    }
}