package com.devsMarr.pos_galeriaemi.domain.service

import com.devsMarr.pos_galeriaemi.domain.model.AppConfig
import com.devsMarr.pos_galeriaemi.domain.model.PairedPrinter
import com.devsMarr.pos_galeriaemi.domain.model.Ticket
import kotlinx.coroutines.flow.StateFlow

sealed class PrinterStatus {
    object Disconnected : PrinterStatus()
    object Connecting : PrinterStatus()
    object Connected : PrinterStatus()
    data class Error(val message: String) : PrinterStatus()
}

interface PrinterService {

    /**
     * Permite a la UI observar en tiempo real si la impresora está conectada,
     * desconectada o si hubo un error.
     */
    val status: StateFlow<PrinterStatus>

    /**
     * Intenta establecer la conexión física con la impresora.
     * @param macAddress La dirección MAC guardada en DataStore.
     */
    suspend fun connect(macAddress: String)

    /**
     * Cierra el puerto de forma segura para ahorrar batería y liberar el hardware.
     */
    suspend fun disconnect()

    /**
     * Imprime un texto genérico. Lo usamos para mandar los comandos ESC/POS
     * ya formateados desde la capa de datos.
     */
    suspend fun printText(text: String)

    /**
     * Función rápida para validar que el hardware responde.
     */
    suspend fun printTestTicket()

    /**
     * Función para imprimir un ticket de venta
     */
    suspend fun printTicket(ticketHead: Ticket, config: AppConfig)

    /**
     * Función para obtener todas las impresoras vinculadas
     */
    fun getPairedPrinters(): List<PairedPrinter>
}