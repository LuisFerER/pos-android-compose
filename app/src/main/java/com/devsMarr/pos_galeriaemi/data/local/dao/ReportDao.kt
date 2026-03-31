package com.devsMarr.pos_galeriaemi.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.devsMarr.pos_galeriaemi.data.local.entity.CategorySaleTuple
import com.devsMarr.pos_galeriaemi.data.local.entity.ProductSaleTuple
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {

    @Query("""
        SELECT 
            c.id AS categoryId, 
            c.name AS categoryName, 
            c.color AS categoryColor, 
            SUM(td.subtotal) AS totalSales
        FROM ticket_heads th
        INNER JOIN ticket_details td ON th.id = td.ticketId
        INNER JOIN products p ON td.productId = p.id
        INNER JOIN categories c ON p.categoryId = c.id
        WHERE th.timestamp >= :startOfDay 
          AND th.timestamp <= :endOfDay
          AND th.status = 'COMPLETED'
        GROUP BY c.id
        ORDER BY totalSales DESC
    """)
    fun getDailySalesByCategory(startOfDay: Long, endOfDay: Long): Flow<List<CategorySaleTuple>>

    @Query("""
        SELECT 
            p.name AS productName,
            SUM(td.quantity) AS totalQuantity,
            SUM(td.subtotal) AS totalSales
        FROM ticket_heads th
        INNER JOIN ticket_details td ON th.id = td.ticketId
        INNER JOIN products p ON td.productId = p.id
        WHERE th.timestamp >= :startOfDay 
          AND th.timestamp <= :endOfDay
          AND th.status = 'COMPLETED'
          AND p.categoryId = :categoryId
        GROUP BY p.id
        ORDER BY totalSales DESC
    """)
    suspend fun getDailySalesByProduct(categoryId: Long, startOfDay: Long, endOfDay: Long): List<ProductSaleTuple>

}