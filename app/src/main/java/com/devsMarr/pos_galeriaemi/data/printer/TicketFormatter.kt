package com.devsMarr.pos_galeriaemi.data.printer

import java.util.Locale

object TicketFormatter {

    private fun getMaxChars(paperWidth: Int): Int = if (paperWidth == 80) 48 else 32

    /**
     * Crea una línea divisoria para el ticket.
     */
    fun createDivider(paperWidth: Int = 58): String {
        return "-".repeat(getMaxChars(paperWidth)) + "\n"
    }

    /**
     * Crea una fila con texto alineado a la izquierda.
     */
    fun createRow(leftText: String, rightText: String, paperWidth: Int = 58): String {
        val maxChars = getMaxChars(paperWidth)
        val safeLeftText = if (leftText.length > (maxChars - rightText.length - 1)) {
            leftText.substring(0, maxChars - rightText.length - 2) + " "
        } else {
            leftText
        }
        val emptySpaces = maxChars - safeLeftText.length - rightText.length
        val spaces = " ".repeat(Math.max(0, emptySpaces))
        return "$safeLeftText$spaces$rightText\n"
    }

    /**
     * Formatea la cantidad a dos decimales.
     */
    fun formatQuantity(qty: Double): String {
        return String.format(Locale.US, "%.2f", qty)
    }
}