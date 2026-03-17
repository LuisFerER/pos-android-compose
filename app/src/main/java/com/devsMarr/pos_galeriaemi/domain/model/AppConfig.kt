package com.devsMarr.pos_galeriaemi.domain.model

data class AppConfig(
    val businessName: String = "",
    val address: String = "",
    val phone: String = "",
    val ticketFooter: String = "¡Gracias por su preferencia!",
    val printerMacAddress: String = "",
    val paperWidth: Int = 58, // 58mm es el estándar de impresoras pequeñas
    val isDarkMode: Boolean = false
)