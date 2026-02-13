package com.devsMarr.pos_galeriaemi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.devsMarr.pos_galeriaemi.data.local.entity.TicketDetailEntity

@Dao
interface TicketDetailDao {

    // INSERTAR LISTA: Recibe una lista de productos y los guarda todos juntos.
    // Retorna una lista de IDs generados
    @Insert
    suspend fun insertDetails(details: List<TicketDetailEntity>): List<Long>

    // LEER: Obtener todos los productos que pertenecen a un ticket específico.
    @Query("SELECT * FROM ticket_details WHERE ticketId = :ticketId")
    suspend fun getDetailsByTicketId(ticketId: Long): List<TicketDetailEntity>

}