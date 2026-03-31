package com.devsMarr.pos_galeriaemi.data.printer

import android.util.Log
import com.devsMarr.pos_galeriaemi.domain.model.AppConfig
import com.devsMarr.pos_galeriaemi.domain.model.PairedPrinter
import com.devsMarr.pos_galeriaemi.domain.model.Ticket
import com.devsMarr.pos_galeriaemi.domain.service.PrinterService
import com.devsMarr.pos_galeriaemi.domain.service.PrinterStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class FakePrinterManager @Inject constructor() : PrinterService {

    private val _status = MutableStateFlow<PrinterStatus>(PrinterStatus.Disconnected)
    override val status: StateFlow<PrinterStatus> = _status.asStateFlow()

    override fun getPairedPrinters(): List<PairedPrinter> {
        // Inventamos dispositivos que aparecerán en el menú de la UI
        return listOf(
            PairedPrinter("Impresora Falsa (Emulador)", "00:11:22:33:44:55"),
            PairedPrinter("Cocina Fake", "AA:BB:CC:DD:EE:FF")
        )
    }

    override suspend fun connect(macAddress: String) {
        _status.value = PrinterStatus.Connecting
        delay(1500) // Simulamos el tiempo que tarda el Bluetooth en conectar

        if (macAddress == "00:11:22:33:44:55" || macAddress == "AA:BB:CC:DD:EE:FF") {
            _status.value = PrinterStatus.Connected
            Log.d("FakePrinter", "✅ CONECTADO A: $macAddress")
        } else {
            _status.value = PrinterStatus.Error("No se encontró la impresora en el emulador.")
        }
    }

    override suspend fun disconnect() {
        _status.value = PrinterStatus.Disconnected
        Log.d("FakePrinter", "🔌 DESCONECTADO")
    }

    override suspend fun printText(text: String) {
        if (_status.value == PrinterStatus.Connected) {
            // Imprimimos el texto crudo en el Logcat (quitando saltos de línea extra para que se lea mejor)
            Log.d("FakePrinter", text.trimEnd())
        }
    }

    override suspend fun printTestTicket() {
        if (_status.value == PrinterStatus.Connected) {
            Log.d("FakePrinter", "\n===============================")
            Log.d("FakePrinter", "      PRUEBA DE IMPRESION      ")
            Log.d("FakePrinter", "===============================")
            Log.d("FakePrinter", "Hardware: Emulador Android")
            Log.d("FakePrinter", "Status: EXITOSO")
            Log.d("FakePrinter", "===============================\n")
        }
    }

    override suspend fun printTicket(ticket: Ticket, config: AppConfig) {
        if (_status.value != PrinterStatus.Connected) return

        // Aquí usamos la misma lógica de formato que diseñamos antes,
        // pero enviándola a nuestra función printText falsa
        Log.d("FakePrinter", "\n--- INICIANDO IMPRESIÓN DE TICKET ---")

        val paperWidth = config.paperWidth
        printText("          ${config.businessName}          ")
        printText(TicketFormatter.createDivider(paperWidth))

        ticket.details.forEach { detail ->
            val qtyStr = TicketFormatter.formatQuantity(detail.quantity)
            val left = "${qtyStr}x ${detail.productNameSnapshot}"
            val right = String.format("$%.2f", detail.subtotal)
            printText(TicketFormatter.createRow(left, right, paperWidth))
        }

        printText(TicketFormatter.createDivider(paperWidth))
        printText("TOTAL: $${ticket.totalAmount}")
        printText("\n${config.ticketFooter}")
        Log.d("FakePrinter", "--- FIN DEL TICKET ---\n")
    }
}