package com.devsMarr.pos_galeriaemi.ui.navigation

// Una clase sellada para tener las rutas bien organizadas
sealed class Screen(val route: String) {
    object Inventory : Screen("inventory_screen")
    object AddCategory : Screen("add_category_screen")
    object AddProduct : Screen("add_product_screen")

    // Esta ruta es especial porque necesita recibir el ID del producto a editar
    object EditProduct : Screen("edit_product_screen/{productId}") {
        fun createRoute(productId: Long) = "edit_product_screen/$productId"
    }
}