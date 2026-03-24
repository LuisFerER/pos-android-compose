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
    savedStateHandle: SavedStateHandle // Atrapa los argumentos de la navegación
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryFormUiState())
    val uiState: StateFlow<CategoryFormUiState> = _uiState.asStateFlow()

    init {
        // Atrapamos el ID que viene desde PosNavigation.kt
        // Ojo: "categoryId" debe llamarse exactamente igual que en navArgument("categoryId")
        val categoryId = savedStateHandle.get<Long>("categoryId")

        if (categoryId != null && categoryId != -1L) {
            // ¡Es una edición! Vamos por los datos a la BD
            viewModelScope.launch {
                val category = categoryRepository.getCategoryById(categoryId)

                if (category != null) {
                    // Rellenamos el estado con los datos existentes
                    _uiState.update { currentState ->
                        currentState.copy(
                            id = category.id,
                            name = category.name,
                            color = category.color
                        )
                    }
                }
            }
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

        if (currentState.name.isBlank()) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val category = Category(
                id = currentState.id ?: 0L,
                name = currentState.name.trim(),
                color = currentState.color,
                sortOrder = 0
            )

            // Room es inteligente: si el ID es 0, hace un INSERT.
            // Si el ID ya existe (porque lo rellenamos en el init), hace un UPDATE.
            if (currentState.id == null || currentState.id == 0L) {
                categoryRepository.insertCategory(category)
            } else {
                categoryRepository.updateCategory(category)
            }

            _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }
}