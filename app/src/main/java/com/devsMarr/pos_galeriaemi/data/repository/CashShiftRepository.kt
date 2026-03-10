package com.devsMarr.pos_galeriaemi.data.repository

import com.devsMarr.pos_galeriaemi.data.local.dao.CashShiftDao
import com.devsMarr.pos_galeriaemi.data.mapper.toDomain
import com.devsMarr.pos_galeriaemi.data.mapper.toEntity
import com.devsMarr.pos_galeriaemi.domain.model.CashShift
import com.devsMarr.pos_galeriaemi.domain.model.ShiftStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CashShiftRepository @Inject constructor(
    private val cashShiftDao: CashShiftDao
) {

    // Obtener la caja activa (si existe)
    suspend fun getCurrentOpenShift(): CashShift? {
        return cashShiftDao.getLastOpenShift()?.toDomain()
    }

    // Historial de turnos en tiempo real para tu pantalla de Administrador
    fun getAllShifts(): Flow<List<CashShift>> {
        return cashShiftDao.getAllShifts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // ABRIR CAJA: Valida que no haya otra abierta
    suspend fun openShift(userId: Long, startingCash: Double): Result<Long> {
        val currentShift = cashShiftDao.getLastOpenShift()

        if (currentShift != null) {
            return Result.failure(Exception("Ya existe una caja abierta. Ciérrala primero."))
        }

        val newShift = CashShift(
            openedById = userId,
            startingCash = startingCash,
            status = ShiftStatus.OPEN,
            startDate = System.currentTimeMillis()
        )

        val shiftId = cashShiftDao.insertShift(newShift.toEntity())
        return Result.success(shiftId)
    }

    // CERRAR CAJA: Calcula los descuadres
    suspend fun closeShift(
        userId: Long,
        finalCashCalculated: Double,
        finalCashReal: Double,
        notes: String? = null
    ): Result<Unit> {
        val currentShiftEntity = cashShiftDao.getLastOpenShift()
            ?: return Result.failure(Exception("No hay ninguna caja abierta para cerrar."))

        val currentShift = currentShiftEntity.toDomain()

        // Matemáticas: Lo que el cajero contó menos lo que el sistema esperaba
        // (Negativo = faltó dinero, Positivo = sobró dinero, Cero = Perfecto)
        val difference = finalCashReal - finalCashCalculated

        val closedShift = currentShift.copy(
            status = ShiftStatus.CLOSED,
            closedById = userId,
            endDate = System.currentTimeMillis(),
            finalCashCalculated = finalCashCalculated,
            finalCashReal = finalCashReal,
            difference = difference,
            notes = notes
        )

        cashShiftDao.updateShift(closedShift.toEntity())
        return Result.success(Unit)
    }
}