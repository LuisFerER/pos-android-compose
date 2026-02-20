package com.devsMarr.pos_galeriaemi.ui.presentation.category_form

// Estado para la pantalla de crear/editar categoría
data class CategoryFormUiState(
    val id: Long? = null, // null significa que estamos creando una nueva
    val name: String = "",
    val color: Int = 0xFF4CAF50.toInt(), // Un color verde por defecto en formato ARGB

    // Control de la pantalla
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false // Para avisarle a la UI que ya guardó y debe volver atrás
)