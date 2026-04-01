package com.devsMarr.pos_galeriaemi.data.export

import android.content.Context
import com.devsMarr.pos_galeriaemi.domain.model.Ticket
import com.devsMarr.pos_galeriaemi.domain.service.ExcelExportService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ExcelExportManager @Inject constructor(
    @ApplicationContext private val context: Context
) : ExcelExportService {

    override suspend fun exportHistoryToExcel(tickets: List<Ticket>): File? = withContext(Dispatchers.IO) {
        try {
            // Apunta a la carpeta "exports"
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) {
                exportDir.mkdirs() // Crea la carpeta si no existe
            }

            // Nombra el archivo
            val fileName = "Historial_Ventas_${System.currentTimeMillis()}.csv"
            val file = File(exportDir, fileName)

            // Escribe el contenido usando FileWriter
            FileWriter(file).use { writer ->
                // Esto le dice a Excel que el archivo es UTF-8
                writer.write('\ufeff'.toString())

                // Escribe los Encabezados de las columnas
                writer.append("Folio,Fecha,Hora,Método de Pago,Estatus,Producto,Cantidad,P. Unitario,Subtotal Producto,Total Ticket\n")

                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                // Itera sobre los tickets y sus detalles
                tickets.forEach { ticket ->
                    val dateStr = dateFormat.format(Date(ticket.timestamp))
                    val timeStr = timeFormat.format(Date(ticket.timestamp))

                    if (ticket.details.isEmpty()) {
                        // Si por algún error un ticket no tiene productos, imprimimos solo el ticket
                        writer.append("${ticket.id},$dateStr,$timeStr,${ticket.paymentMethod},${ticket.status},Ninguno,0,0.00,0.00,${ticket.totalAmount}\n")
                    } else {
                        // Imprime una fila por cada producto dentro del ticket
                        ticket.details.forEach { detail ->
                            // Limpia el nombre del producto por si el usuario le puso comas al crearlo
                            val safeProductName = detail.productNameSnapshot.replace(",", " ")

                            writer.append("${ticket.id},")
                            writer.append("$dateStr,")
                            writer.append("$timeStr,")
                            writer.append("${ticket.paymentMethod},")
                            writer.append("${ticket.status},")
                            writer.append("$safeProductName,")
                            writer.append("${detail.quantity},")
                            writer.append("${detail.unitPriceSnapshot},")
                            writer.append("${detail.subtotal},")
                            writer.append("${ticket.totalAmount}\n")
                        }
                    }
                }
            }

            // Retorna el archivo
            return@withContext file

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}