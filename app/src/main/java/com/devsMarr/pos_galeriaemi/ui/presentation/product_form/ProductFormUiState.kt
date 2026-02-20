package com.devsMarr.pos_galeriaemi.ui.presentation.product_form

import com.devsMarr.pos_galeriaemi.domain.model.Category

data class ProductFormUiState(
    // Datos del producto
    val id: Long? = null, // null = Crear nuevo, con número = Editar
    val name: String = "",
    val price: String = "", // Usamos String porque los TextFields de Compose funcionan mejor así
    val stock: String = "",
    val categoryId: Long? = null,
    val isVariablePrice: Boolean = false,

    // Datos para la UI (Menús y validaciones)
    val categories: List<Category> = emptyList(), // Lista para llenar el menú desplegable
    val isCategoriesLoading: Boolean = true, // Para mostrar un "Cargando..." en el menú si es necesario
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false, // Para cerrar la pantalla al terminar
    val errorMessage: String? = null // Para mostrar errores (ej. "Falta el precio")
)