package com.devsMarr.pos_galeriaemi.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT // Para no permitir borrar si hay productos en una categoria
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val categoryId: Long, // Llave Foranea de CategoryEntity

    val name: String,

    val price: Double,

    val stock: Double = 0.0,

    val isVariablePrice: Boolean = false, // Para determinar si se puede variar el precio al momento de la venta

    val isActive: Boolean = true
)