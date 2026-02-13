package com.devsMarr.pos_galeriaemi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.devsMarr.pos_galeriaemi.data.local.entity.TicketHeadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketHeadDao {

    // Insertar la venta (Retorna el ID nuevo, necesario para guardar los productos después)
    @Insert
    suspend fun insertTicketHead(ticket: TicketHeadEntity): Long

    // Obtener una venta específica
    @Query("SELECT * FROM ticket_heads WHERE id = :id")
    suspend fun getTicketById(id: Long): TicketHeadEntity?

    // Obtener todas las ventas de un turno específico
    @Query("SELECT * FROM ticket_heads WHERE shiftId = :shiftId ORDER BY timestamp DESC")
    fun getTicketsByShift(shiftId: Long): Flow<List<TicketHeadEntity>>

    // Actualizar (Usado principalmente para cancelar una venta: cambiar status a 'CANCELLED')
    @Update
    suspend fun updateTicketHead(ticket: TicketHeadEntity): Int

    // --- REPORTES Y ESTADÍSTICAS ---

    // Suma total de ventas de un turno (Ignorando las canceladas)
    // Devuelve Flow para que si haces una venta nueva, el numerito del total se actualice solo en pantalla.
    @Query("SELECT SUM(totalAmount) FROM ticket_heads WHERE shiftId = :shiftId AND status = 'COMPLETED'")
    fun getTotalSalesByShift(shiftId: Long): Flow<Double?>
}