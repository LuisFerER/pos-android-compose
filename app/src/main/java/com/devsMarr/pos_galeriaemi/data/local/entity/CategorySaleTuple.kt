package com.devsMarr.pos_galeriaemi.data.local.entity

data class CategorySaleTuple(
    val categoryId: Long,
    val categoryName: String,
    val categoryColor: Int,
    val totalSales: Double? // Puede ser null si la suma da vacío
)