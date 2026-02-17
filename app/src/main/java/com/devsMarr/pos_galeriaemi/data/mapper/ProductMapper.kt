package com.devsMarr.pos_galeriaemi.data.mapper

import com.devsMarr.pos_galeriaemi.data.local.entity.ProductEntity
import com.devsMarr.pos_galeriaemi.domain.model.Product

// Base de Datos -> A Dominio
fun ProductEntity.toDomain(): Product {
    return Product(
        id = this.id,
        categoryId = this.categoryId,
        name = this.name,
        price = this.price,
        stock = this.stock,
        isVariablePrice = this.isVariablePrice,
        isActive = this.isActive
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = this.id,
        categoryId = this.categoryId,
        name = this.name,
        price = this.price,
        stock = this.stock,
        isVariablePrice = this.isVariablePrice,
        isActive = this.isActive
    )
}

fun List<ProductEntity>.toDomain(): List<Product> {
    return this.map { it.toDomain() }
}