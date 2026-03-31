package com.devsMarr.pos_galeriaemi.domain.model

data class CategoryDailySale(
    val categoryId: Long,
    val categoryName: String,
    val categoryColor: Int,
    val totalAmount: Double
)