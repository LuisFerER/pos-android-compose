package com.devsMarr.pos_galeriaemi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.devsMarr.pos_galeriaemi.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    // Obtener todos los productos activos
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActiveProducts(): Flow<List<ProductEntity>>

    // Obtener productos de una categoría específica
    @Query("SELECT * FROM products WHERE categoryId = :categoryId AND isActive = 1 ORDER BY name ASC")
    fun getProductsByCategory(categoryId: Long): Flow<List<ProductEntity>>

    // Buscar productos por nombre
    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' AND isActive = 1 ORDER BY name ASC")
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    // Obtener un producto específico por ID
    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    // SOFT DELETE: No borra el registro, solo se marca como inactivo.
    @Query("UPDATE products SET isActive = 0 WHERE id = :productId")
    suspend fun deleteProduct(productId: Long)
}