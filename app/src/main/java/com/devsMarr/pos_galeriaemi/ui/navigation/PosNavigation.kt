package com.devsMarr.pos_galeriaemi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.devsMarr.pos_galeriaemi.ui.presentation.category_form.CategoryFormScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.inventory.ProductListScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.product_form.ProductFormScreen

@Composable
fun PosNavigation() {
    // El controlador que sabe cómo viajar entre pantallas
    val navController = rememberNavController()

    // El NavHost es el contenedor donde se dibujan las pantallas
    NavHost(
        navController = navController,
        startDestination = Screen.Inventory.route // La pantalla inicial
    ) {

        // Pantalla de Inventario (Lista)
        composable(route = Screen.Inventory.route) {
            ProductListScreen(
                onNavigateToAddProduct = {
                    navController.navigate(Screen.AddProduct.route)
                },
                onNavigateToEditProduct = { productId ->
                    navController.navigate(Screen.EditProduct.createRoute(productId))
                }
            )
        }

        // Pantalla de Nueva Categoría
        composable(route = Screen.AddCategory.route) {
            CategoryFormScreen(
                onNavigateBack = { navController.popBackStack() } // popBackStack = regresar
            )
        }

        // Pantalla de Nuevo Producto
        composable(route = Screen.AddProduct.route) {
            ProductFormScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Pantalla de Editar Producto (Recibe un ID)
        composable(
            route = Screen.EditProduct.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.LongType }
            )
        ) {
            ProductFormScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}