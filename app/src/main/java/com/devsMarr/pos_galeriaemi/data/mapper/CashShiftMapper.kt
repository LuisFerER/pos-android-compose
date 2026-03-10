package com.devsMarr.pos_galeriaemi.data.mapper

import com.devsMarr.pos_galeriaemi.data.local.entity.CashShiftEntity
import com.devsMarr.pos_galeriaemi.domain.model.CashShift
import com.devsMarr.pos_galeriaemi.domain.model.ShiftStatus

fun CashShiftEntity.toDomain(): CashShift {
    return CashShift(
        id = this.id,
        openedById = this.openedById,
        closedById = this.closedById,
        status = try { ShiftStatus.valueOf(this.status) } catch (e: Exception) { ShiftStatus.CLOSED },
        startDate = this.startDate,
        endDate = this.endDate,
        startingCash = this.startingCash,
        finalCashCalculated = this.finalCashCalculated,
        finalCashReal = this.finalCashReal,
        difference = this.difference,
        notes = this.notes
    )
}

fun CashShift.toEntity(): CashShiftEntity {
    return CashShiftEntity(
        id = this.id,
        openedById = this.openedById,
        closedById = this.closedById,
        status = this.status.name,
        startDate = this.startDate,
        endDate = this.endDate,
        startingCash = this.startingCash,
        finalCashCalculated = this.finalCashCalculated,
        finalCashReal = this.finalCashReal,
        difference = this.difference,
        notes = this.notes
    )
}