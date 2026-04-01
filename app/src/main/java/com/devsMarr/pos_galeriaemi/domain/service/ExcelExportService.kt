package com.devsMarr.pos_galeriaemi.domain.service

import com.devsMarr.pos_galeriaemi.domain.model.Ticket
import java.io.File

interface ExcelExportService {
    /**
     * Recibe una lista de tickets y genera un archivo compatible con Excel.
     * @return El archivo físico (File) en la memoria caché, o null si ocurre un error.
     */
    suspend fun exportHistoryToExcel(tickets: List<Ticket>): File?
}