package com.devsMarr.pos_galeriaemi.data.repository

import com.devsMarr.pos_galeriaemi.data.local.dao.ProductDao
import com.devsMarr.pos_galeriaemi.data.mapper.toDomain
import com.devsMarr.pos_galeriaemi.data.mapper.toEntity
import com.devsMarr.pos_galeriaemi.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao
) {

    // Obtener lista de productos activos
    fun getAllActiveProducts(): Flow<List<Product>> {
        return productDao.getAllActiveProducts().map { entities ->
            entities.map { entity ->
                entity.toDomain()
            }
        }
    }

    // Filtrar por categoría
    fun getProductsByCategory(categoryId: Long): Flow<List<Product>> {
        return productDao.getProductsByCategory(categoryId).map {entities ->
            entities.map {entity -> entity.toDomain()}
        }
    }

    // Buscador por nombre
    fun searchProducts(query: String): Flow<List<Product>> {
        return productDao.searchProducts(query).map {entities ->
            entities.map {entity -> entity.toDomain()}
        }
    }

    // Obtener un producto individual
    suspend fun getProductById(id: Long): Product? {
        return productDao.getProductById(id)?.toDomain()
    }

    // Insertar o Actualizar
    suspend fun insertProduct(product: Product) {
        productDao.insertProduct(product.toEntity())
    }

    // Actualizar datos
    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product.toEntity())
    }

    suspend fun deleteProduct(productId: Long) {
        productDao.deleteProduct(productId)
    }
}