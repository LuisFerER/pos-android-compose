package com.devsMarr.pos_galeriaemi.data.export

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.devsMarr.pos_galeriaemi.domain.model.Ticket
import com.devsMarr.pos_galeriaemi.domain.service.PdfCategoryData
import com.devsMarr.pos_galeriaemi.domain.service.PdfExportService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class PdfExportManager @Inject constructor(
    @ApplicationContext private val context: Context
) : PdfExportService {

    // Tamaño A4 estándar a 72 PPI
    private val PAGE_WIDTH = 595
    private val PAGE_HEIGHT = 842

    override suspend fun exportDailyReport(
        businessName: String,
        dateString: String,
        totalSales: Double,
        categories: List<PdfCategoryData>
    ): File = withContext(Dispatchers.IO) {

        val document = PdfDocument()
        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        // --- Pinceles ---
        val businessNamePaint = Paint().apply { typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); textSize = 28f; color = Color.BLACK; textAlign = Paint.Align.CENTER }
        val titlePaint = Paint().apply { typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); textSize = 20f; color = Color.DKGRAY; textAlign = Paint.Align.CENTER }

        val textPaint = Paint().apply { typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL); textSize = 14f; color = Color.BLACK }
        val smallTextPaint = Paint().apply { typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL); textSize = 12f; color = Color.DKGRAY }
        val boldPaint = Paint().apply { typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); textSize = 14f; color = Color.BLACK }
        val linePaint = Paint().apply { color = Color.LTGRAY; strokeWidth = 1f }

        var currentY = 60f // Empezamos un poquito más abajo para dar margen superior
        val marginX = 50f

        // FUNCIÓN INTERNA: Verifica si necesitamos una página nueva
        fun checkPageBreak(requiredSpace: Float) {
            if (currentY + requiredSpace > PAGE_HEIGHT - 50f) {
                document.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                currentY = 60f
            }
        }

        // --- CABECERA ---
        // 1. Nombre del Negocio
        canvas.drawText(businessName, PAGE_WIDTH / 2f, currentY, businessNamePaint)
        currentY += 35f

        // 2. Título del Reporte
        canvas.drawText("Reporte Diario de Ventas", PAGE_WIDTH / 2f, currentY, titlePaint)
        currentY += 40f

        // 3. Detalles (Fecha y Total)
        canvas.drawText("Fecha: $dateString", marginX, currentY, textPaint)
        currentY += 30f
        canvas.drawText(String.format("Venta Total del Día: $%.2f", totalSales), marginX, currentY, boldPaint)
        currentY += 20f
        canvas.drawLine(marginX, currentY, PAGE_WIDTH - marginX, currentY, linePaint)
        currentY += 30f

        // --- ITERAR SOBRE CATEGORÍAS Y PRODUCTOS ---
        for (category in categories) {
            checkPageBreak(40f) // Revisamos si cabe el título de la categoría

            // 1. Título de la Categoría
            canvas.drawText(category.categoryName, marginX, currentY, boldPaint)
            val catTotalStr = String.format("$%.2f", category.categoryTotal)
            val catTotalWidth = boldPaint.measureText(catTotalStr)
            canvas.drawText(catTotalStr, PAGE_WIDTH - marginX - catTotalWidth, currentY, boldPaint)
            currentY += 25f

            // 2. Productos de esta categoría
            for (product in category.products) {
                checkPageBreak(25f) // Revisamos si cabe este producto

                // Dibujamos el producto ligeramente a la derecha (marginX + 20f)
                canvas.drawText(product.quantityAndName, marginX + 20f, currentY, smallTextPaint)

                // Total del producto a la derecha
                val prodTotalStr = String.format("$%.2f", product.subtotal)
                val prodTotalWidth = smallTextPaint.measureText(prodTotalStr)
                canvas.drawText(prodTotalStr, PAGE_WIDTH - marginX - prodTotalWidth, currentY, smallTextPaint)

                currentY += 20f
            }

            // Línea divisoria entre categorías
            currentY += 10f
            checkPageBreak(20f)
            canvas.drawLine(marginX, currentY, PAGE_WIDTH - marginX, currentY, linePaint)
            currentY += 20f
        }

        document.finishPage(page)

        // --- GUARDADO ---
        val directory = File(context.cacheDir, "exports") // Usando la carpeta del compañero
        if (!directory.exists()) directory.mkdirs()

        val safeDate = dateString.replace("/", "-").replace(" ", "_")
        val file = File(directory, "Reporte_Diario_$safeDate.pdf")

        try {
            document.writeTo(FileOutputStream(file))
        } finally {
            document.close()
        }

        return@withContext file
    }

    override suspend fun exportHistory(dateRangeString: String, tickets: List<Ticket>): File {
        // Aquí se implementará la tabla del historial de tickets.
        throw NotImplementedError("Función en construcción")
    }
}