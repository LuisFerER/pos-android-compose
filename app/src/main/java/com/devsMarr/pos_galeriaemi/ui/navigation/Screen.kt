package com.devsMarr.pos_galeriaemi.ui.navigation

sealed class Screen(val route: String) {
    // --- Rutas Principales ---
    object Login : Screen("login_screen")
    object Pos : Screen("pos_screen")
    object AdminDashboard : Screen("admin_dashboard_screen")
    object TicketHistory : Screen("ticket_history_screen")

    // --- Rutas de Inventario y Catálogo ---
    object Inventory : Screen("inventory_screen")
    object AddCategory : Screen("add_category_screen")
    object AddProduct : Screen("add_product_screen")

    object EditProduct : Screen("edit_product_screen/{productId}") {
        fun createRoute(productId: Long) = "edit_product_screen/$productId"
    }

    // --- Rutas de Empleados ---
    object Users : Screen("users_screen")

    object AddUser : Screen("add_user_screen")

    object EditUser : Screen("edit_user_screen/{userId}") {
        fun createRoute(userId: Long) = "edit_user_screen/$userId"
    }

    // --- Rutas de Configuración ---
    object Settings : Screen("settings_screen")
}