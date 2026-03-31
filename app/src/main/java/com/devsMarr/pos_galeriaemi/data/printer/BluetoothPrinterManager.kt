package com.devsMarr.pos_galeriaemi.data.printer

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.devsMarr.pos_galeriaemi.domain.model.AppConfig
import com.devsMarr.pos_galeriaemi.domain.model.PairedPrinter
import com.devsMarr.pos_galeriaemi.domain.model.Ticket
import com.devsMarr.pos_galeriaemi.domain.service.PrinterService
import com.devsMarr.pos_galeriaemi.domain.service.PrinterStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStream
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothPrinterManager @Inject constructor(
    @ApplicationContext private val context: Context
) : PrinterService {

    private val _status = MutableStateFlow<PrinterStatus>(PrinterStatus.Disconnected)
    override val status: StateFlow<PrinterStatus> = _status.asStateFlow()

    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    // UUID estándar universal para perfiles de puerto serial
    private val PRINTER_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    // Obtener el adaptador Bluetooth del sistema de forma segura
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    @SuppressLint("MissingPermission")
    override suspend fun connect(macAddress: String) {
        withContext(Dispatchers.IO) {
            try {
                _status.value = PrinterStatus.Connecting

                // Doble validación de seguridad
                if (!hasBluetoothPermissions()) {
                    _status.value = PrinterStatus.Error("Permisos de Bluetooth denegados.")
                    return@withContext
                }

                if (bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled) {
                    _status.value = PrinterStatus.Error("El Bluetooth está apagado o no es compatible.")
                    return@withContext
                }

                if (!BluetoothAdapter.checkBluetoothAddress(macAddress)) {
                    _status.value = PrinterStatus.Error("Dirección MAC inválida.")
                    return@withContext
                }

                // Localiza el dispositivo
                val device: BluetoothDevice = bluetoothAdapter!!.getRemoteDevice(macAddress)

                // Cancelar el descubrimiento antes de conectar acelera el proceso
                bluetoothAdapter!!.cancelDiscovery()

                // Abrir el socket de comunicación
                bluetoothSocket = device.createRfcommSocketToServiceRecord(PRINTER_UUID)
                bluetoothSocket?.connect()
                outputStream = bluetoothSocket?.outputStream

                _status.value = PrinterStatus.Connected

            } catch (e: IOException) {
                // Si algo falla, limpiamos los recursos y avisamos a la UI
                disconnect()
                _status.value = PrinterStatus.Error("Error de conexión: Verifica que la impresora esté encendida.")
            }
        }
    }

    override suspend fun disconnect() {
        withContext(Dispatchers.IO) {
            try {
                outputStream?.close()
                bluetoothSocket?.close()
            } catch (e: Exception) {
                // Ignora errores al cerrar, el objetivo es liberar memoria
            } finally {
                outputStream = null
                bluetoothSocket = null
                _status.value = PrinterStatus.Disconnected
            }
        }
    }

    override suspend fun printText(text: String) {
        withContext(Dispatchers.IO) {
            if (_status.value != PrinterStatus.Connected || outputStream == null) {
                return@withContext // No podemos imprimir si no hay conexión
            }

            try {
                // Usamos codificación ISO-8859-1 para que la impresora respete acentos y la letra 'ñ'
                val bytes = text.toByteArray(Charset.forName("ISO-8859-1"))
                outputStream?.write(bytes)
                outputStream?.flush() // Obliga al hardware a vaciar el buffer y procesar los bytes
            } catch (e: IOException) {
                _status.value = PrinterStatus.Error("Error al imprimir. Se perdió la conexión.")
                disconnect()
            }
        }
    }

    override suspend fun printTestTicket() {
        // Por ahora solo mandamos texto plano.
        // En la siguiente fase le agregaremos comandos de corte, centrado y tamaño.
        printText("\n--- PRUEBA DE IMPRESION ---\n")
        printText("Galeria Emi\n")
        printText("La conexion Bluetooth es exitosa.\n")
        printText("---------------------------\n")
        printText("\n\n\n") // Saltos de línea para que el papel salga lo suficiente
    }

    override suspend fun printTicket(ticket: Ticket, config: AppConfig) {
        withContext(Dispatchers.IO) {
            if (_status.value != PrinterStatus.Connected || outputStream == null) return@withContext

            try {
                val paperWidth = config.paperWidth

                // Cabecera del Negocio
                outputStream?.write(EscPosCommands.INIT_PRINTER)
                outputStream?.write(EscPosCommands.ALIGN_CENTER)
                outputStream?.write(EscPosCommands.TEXT_SIZE_DOUBLE_HEIGHT)
                outputStream?.write(EscPosCommands.BOLD_ON)
                printText("${config.businessName}\n")

                outputStream?.write(EscPosCommands.TEXT_SIZE_NORMAL)
                outputStream?.write(EscPosCommands.BOLD_OFF)

                // Dirección y Teléfono
                if (config.address.isNotBlank()) printText("${config.address}\n")
                if (config.phone.isNotBlank()) printText("Tel: ${config.phone}\n")

                printText(TicketFormatter.createDivider(paperWidth))

                // Datos del Ticket
                outputStream?.write(EscPosCommands.ALIGN_LEFT)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val dateString = dateFormat.format(Date(ticket.timestamp))

                printText("Ticket: #${ticket.id}\n")
                printText("Fecha: $dateString\n")
                printText(TicketFormatter.createDivider(paperWidth))

                // Detalles de los productos
                for (detail in ticket.details) {
                    val qtyStr = TicketFormatter.formatQuantity(detail.quantity)
                    val left = "${qtyStr}x ${detail.productNameSnapshot}"
                    val right = TicketFormatter.formatQuantity(detail.subtotal)

                    printText(TicketFormatter.createRow(left, right, paperWidth))
                }

                printText(TicketFormatter.createDivider(paperWidth))

                // Totales, Efectivo y Cambio
                outputStream?.write(EscPosCommands.ALIGN_RIGHT)

                // Total en grande
                outputStream?.write(EscPosCommands.BOLD_ON)
                outputStream?.write(EscPosCommands.TEXT_SIZE_DOUBLE_HEIGHT)
                printText(String.format("TOTAL: $%.2f\n", ticket.totalAmount))

                // Efectivo y Cambio en tamaño normal
                outputStream?.write(EscPosCommands.TEXT_SIZE_NORMAL)
                outputStream?.write(EscPosCommands.BOLD_OFF)
                printText(String.format("Recibido: $%.2f\n", ticket.receivedAmount))
                printText(String.format("Cambio: $%.2f\n", ticket.changeAmount))

                // Pie de página
                outputStream?.write(EscPosCommands.ALIGN_CENTER)
                printText("\n${config.ticketFooter}\n")
                printText("\n\n\n") // Espacio para arrancar el ticket

                outputStream?.flush()

            } catch (e: IOException) {
                _status.value = PrinterStatus.Error("Error al escribir los datos en el puerto Bluetooth.")
                disconnect()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun getPairedPrinters(): List<PairedPrinter> {
        // Validamos que tenga permisos. Si no, devuelve una lista vacía para no crashear.
        if (!hasBluetoothPermissions()) {
            return emptyList()
        }

        // Valida que el Bluetooth exista y esté encendido
        if (bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled) {
            return emptyList()
        }

        return try {
            // Obtiene los dispositivos vinculados
            val pairedDevices: Set<BluetoothDevice> = bluetoothAdapter!!.bondedDevices

            // Transformamos la clase BluetoothDevice a PairedPrinter
            pairedDevices.map { device ->
                PairedPrinter(
                    // Si por alguna razón el nombre es nulo, le ponemos un texto por defecto
                    name = device.name ?: "Dispositivo Desconocido",
                    macAddress = device.address
                )
            }
        } catch (e: SecurityException) {
            // Un paracaídas por si el permiso fue revocado justo en el milisegundo que llamamos a la API
            emptyList()
        }
    }

    // --- FUNCIÓN DE UTILIDAD ---
    private fun hasBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
        }
    }
}
