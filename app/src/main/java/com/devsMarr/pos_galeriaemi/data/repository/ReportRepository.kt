package com.devsMarr.pos_galeriaemi.data.repository

import com.devsMarr.pos_galeriaemi.data.local.dao.ReportDao
import com.devsMarr.pos_galeriaemi.data.mapper.toDomain
import com.devsMarr.pos_galeriaemi.domain.model.CategoryDailySale
import com.devsMarr.pos_galeriaemi.domain.model.ProductDailySale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReportRepository @Inject constructor(
    private val reportDao: ReportDao
) {
    fun getDailySalesByCategory(startOfDay: Long, endOfDay: Long): Flow<List<CategoryDailySale>> {
        return reportDao.getDailySalesByCategory(startOfDay, endOfDay)
            .map { tuples ->
                tuples.map { it.toDomain() }
            }
    }

    suspend fun getDailySalesByProduct(categoryId: Long, startOfDay: Long, endOfDay: Long): List<ProductDailySale> {
        val tuples = reportDao.getDailySalesByProduct(categoryId, startOfDay, endOfDay)
        return tuples.map { it.toDomain() }
    }
}