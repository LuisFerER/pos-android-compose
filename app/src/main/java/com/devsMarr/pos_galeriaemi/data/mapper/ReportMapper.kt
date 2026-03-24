package com.devsMarr.pos_galeriaemi.data.mapper

import com.devsMarr.pos_galeriaemi.data.local.entity.CategorySaleTuple
import com.devsMarr.pos_galeriaemi.data.local.entity.ProductSaleTuple
import com.devsMarr.pos_galeriaemi.domain.model.CategoryDailySale
import com.devsMarr.pos_galeriaemi.domain.model.ProductDailySale

fun CategorySaleTuple.toDomain(): CategoryDailySale {
    return CategoryDailySale(
        categoryId = this.categoryId,
        categoryName = this.categoryName,
        categoryColor = this.categoryColor,
        totalAmount = this.totalSales ?: 0.0
    )
}

fun ProductSaleTuple.toDomain(): ProductDailySale {
    return ProductDailySale(
        productName = this.productName,
        quantitySold = this.totalQuantity ?: 0.0,
        totalAmount = this.totalSales ?: 0.0
    )
}