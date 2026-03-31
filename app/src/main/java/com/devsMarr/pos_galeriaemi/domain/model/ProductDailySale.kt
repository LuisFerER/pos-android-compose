package com.devsMarr.pos_galeriaemi.domain.model

data class ProductDailySale(
    val productName: String,
    val quantitySold: Double, // Cuántas unidades se vendieron
    val totalAmount: Double   // Cuánto dinero generó
)