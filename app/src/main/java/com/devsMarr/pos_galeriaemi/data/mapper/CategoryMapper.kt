package com.devsMarr.pos_galeriaemi.data.mapper

import com.devsMarr.pos_galeriaemi.data.local.entity.CategoryEntity
import com.devsMarr.pos_galeriaemi.domain.model.Category

// Base de Datos -> A Dominio
fun CategoryEntity.toDomain(): Category {
    return Category(
        id = this.id,
        name = this.name,
        color = this.color,
        sortOrder = this.sortOrder
    )
}

// Dominio -> A Base de Datos
fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = this.id,
        name = this.name,
        color = this.color,
        sortOrder = this.sortOrder
    )
}

fun List<CategoryEntity>.toDomain(): List<Category> {
    return this.map { it.toDomain() }
}