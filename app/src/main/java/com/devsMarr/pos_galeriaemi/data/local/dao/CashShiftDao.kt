package com.devsMarr.pos_galeriaemi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.devsMarr.pos_galeriaemi.data.local.entity.CashShiftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CashShiftDao {

    // Buscar si hay un turno activo actualmente
    @Query("SELECT * FROM cash_shifts WHERE endDate IS NULL LIMIT 1")
    suspend fun getLastOpenShift(): CashShiftEntity?

    // Historial de turnos
    @Query("SELECT * FROM cash_shifts ORDER BY startDate DESC")
    fun getAllShifts(): Flow<List<CashShiftEntity>>

    // Obtener un turno específico
    @Query("SELECT * FROM cash_shifts WHERE id = :id")
    suspend fun getShiftById(id: Long): CashShiftEntity?

    // Abrir turno
    @Insert
    suspend fun insertShift(shift: CashShiftEntity): Long

    // Cerrar turno (Actualizar con fecha fin y montos finales)
    @Update
    suspend fun updateShift(shift: CashShiftEntity)
}