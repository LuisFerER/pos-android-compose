package com.devsMarr.pos_galeriaemi.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val color: Int, // Entero ARGB
    val sortOrder: Int
)