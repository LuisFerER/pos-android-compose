package com.devsMarr.pos_galeriaemi.domain.service

import com.devsMarr.pos_galeriaemi.domain.model.Ticket
import java.io.File

data class PdfCategoryData(
    val categoryName: String,
    val categoryTotal: Double,
    val products: List<PdfProductData>
)

data class PdfProductData(
    val quantityAndName: String,
    val subtotal: Double
)

interface PdfExportService {

    suspend fun exportDailyReport(
        businessName: String,
        dateString: String,
        totalSales: Double,
        categories: List<PdfCategoryData>
    ): File

    suspend fun exportHistory(
        dateRangeString: String,
        tickets: List<Ticket>
    ): File
}