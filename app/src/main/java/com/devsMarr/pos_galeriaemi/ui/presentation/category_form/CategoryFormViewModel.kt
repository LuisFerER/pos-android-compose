package com.devsMarr.pos_galeriaemi.ui.presentation.category_form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsMarr.pos_galeriaemi.data.repository.CategoryRepository
import com.devsMarr.pos_galeriaemi.domain.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryFormViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle // Para saber si venimos a editar una existente
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryFormUiState())
    val uiState: StateFlow<CategoryFormUiState> = _uiState.asStateFlow()

    init {
        // Buscamos si nos pasaron un ID por navegación
        val categoryId = savedStateHandle.get<Long>("categoryId")
        if (categoryId != null && categoryId != -1L) {
            // Aún no tenemos un getCategoryById en el repositorio, pero si quisieras editar, aquí lo cargarías
            // Por ahora lo dejaremos preparado
        }
    }

    // --- Funciones para que la UI actualice el estado ---
    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun onColorChange(newColor: Int) {
        _uiState.update { it.copy(color = newColor) }
    }

    // --- Función para guardar en la BD ---
    fun saveCategory() {
        val currentState = _uiState.value

        // Validación básica: que el nombre no esté vacío
        if (currentState.name.isBlank()) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            // Creamos el objeto de Dominio
            val category = Category(
                id = currentState.id ?: 0L, // Si es 0, Room crea uno nuevo automáticamente
                name = currentState.name.trim(),
                color = currentState.color,
                sortOrder = 0
            )

            // Guardamos usando el Repositorio
            if (currentState.id == null || currentState.id == 0L) {
                categoryRepository.insertCategory(category)
            } else {
                categoryRepository.updateCategory(category)
            }

            // Avisamos a la UI que fue un éxito
            _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }
}