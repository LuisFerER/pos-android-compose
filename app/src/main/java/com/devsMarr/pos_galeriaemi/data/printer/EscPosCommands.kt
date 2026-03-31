package com.devsMarr.pos_galeriaemi.data.printer

/**
 * Diccionario de comandos ESC/POS estándar para impresoras térmicas genéricas.
 * Cada comando es un arreglo de bytes (Byte Array) que altera el estado del hardware.
 */
object EscPosCommands {

    // --- INICIALIZACIÓN ---
    /** Resetea la impresora a sus valores por defecto */
    val INIT_PRINTER = byteArrayOf(0x1B, 0x40)

    // --- ALINEACIÓN DE TEXTO ---
    /** Alinea el texto a la izquierda*/
    val ALIGN_LEFT = byteArrayOf(0x1B, 0x61, 0x00)

    /** Centra el texto */
    val ALIGN_CENTER = byteArrayOf(0x1B, 0x61, 0x01)

    /** Alinea el texto a la derecha */
    val ALIGN_RIGHT = byteArrayOf(0x1B, 0x61, 0x02)

    // --- FORMATO DE TEXTO ---
    /** Enciende el texto en Negritas */
    val BOLD_ON = byteArrayOf(0x1B, 0x45, 0x01)

    /** Apaga el texto en Negritas */
    val BOLD_OFF = byteArrayOf(0x1B, 0x45, 0x00)

    // --- TAMAÑO DE TEXTO ---
    /** Tamaño de fuente normal */
    val TEXT_SIZE_NORMAL = byteArrayOf(0x1D, 0x21, 0x00)

    /** Doble altura */
    val TEXT_SIZE_DOUBLE_HEIGHT = byteArrayOf(0x1D, 0x21, 0x01)

    /** Doble anchura */
    val TEXT_SIZE_DOUBLE_WIDTH = byteArrayOf(0x1D, 0x21, 0x10)

    /** Doble altura y doble anchura */
    val TEXT_SIZE_BIG = byteArrayOf(0x1D, 0x21, 0x11)

    // --- CONTROL DE PAPEL ---
    /** Imprime el buffer y avanza el papel unas cuantas líneas para que el ticket salga bien */
    val FEED_PAPER = byteArrayOf(0x1B, 0x64, 0x04) // Avanza 4 líneas

    /** * Corta el papel automáticamente. */
    val CUT_PAPER = byteArrayOf(0x1D, 0x56, 0x42, 0x00)
}