package com.devsMarr.pos_galeriaemi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val color: Int, // Guardaremos el color como un entero ARGB

    val sortOrder: Int = 0
)