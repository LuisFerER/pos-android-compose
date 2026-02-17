package com.devsMarr.pos_galeriaemi.domain.model

import java.util.Locale

data class Product (
    val id: Long = 0,
    val categoryId: Long,
    val name: String,
    val price: Double,
    val stock: Double,
    val isVariablePrice: Boolean,
    val isActive: Boolean
) {

    val formattedPrice: String
        get() = if (isVariablePrice && price == 0.0) {
            "A granel/Variable"
        } else {
            "$ ${String.format("%.2f", price)}"
        }

    val formattedStock: String
        get() {
            // Si el decimal es 0 (ej: 10.0), lo muestra como entero (10)
            return if (stock % 1.0 == 0.0) {
                stock.toInt().toString()
            } else {
                String.format("%.2f", stock)
            }
        }
}