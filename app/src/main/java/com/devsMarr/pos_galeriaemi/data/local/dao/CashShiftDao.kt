package com.devsMarr.pos_galeriaemi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.devsMarr.pos_galeriaemi.data.local.entity.CashShiftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CashShiftDao {

    // Buscar si hay un turno activo usando la columna status
    @Query("SELECT * FROM cash_shifts WHERE status = 'OPEN' LIMIT 1")
    suspend fun getLastOpenShift(): CashShiftEntity?

    // Historial de turnos (Excelente uso de Flow para la UI reactiva)
    @Query("SELECT * FROM cash_shifts ORDER BY startDate DESC")
    fun getAllShifts(): Flow<List<CashShiftEntity>>

    // Obtener un turno específico
    @Query("SELECT * FROM cash_shifts WHERE id = :id")
    suspend fun getShiftById(id: Long): CashShiftEntity?

    // Abrir turno
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShift(shift: CashShiftEntity): Long

    // Cerrar turno
    @Update
    suspend fun updateShift(shift: CashShiftEntity)
}