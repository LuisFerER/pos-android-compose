package com.devsMarr.pos_galeriaemi.domain.model

data class CashShift(
    val id: Long = 0,
    val openedById: Long,
    val closedById: Long? = null,
    val status: ShiftStatus = ShiftStatus.OPEN,

    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,

    val startingCash: Double,
    val finalCashCalculated: Double? = null,
    val finalCashReal: Double? = null,
    val difference: Double? = null,
    val notes: String? = null
)