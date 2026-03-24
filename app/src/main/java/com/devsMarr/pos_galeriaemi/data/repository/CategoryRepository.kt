package com.devsMarr.pos_galeriaemi.data.repository

import com.devsMarr.pos_galeriaemi.data.local.dao.CategoryDao
import com.devsMarr.pos_galeriaemi.data.mapper.toDomain
import com.devsMarr.pos_galeriaemi.data.mapper.toEntity
import com.devsMarr.pos_galeriaemi.domain.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    // Obtener todas las categorías
    fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // Insertar nueva categoría
    suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category.toEntity())
    }

    // Actualizar categoría existente
    suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category.toEntity())
    }

    // Eliminar categoría
    // Nota: Tu DAO pide la entidad completa para borrar, no el ID.
    suspend fun deleteCategory(categoryId: Long) {
        categoryDao.deleteCategoryById(categoryId)
    }

    suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)?.toDomain()
    }
}