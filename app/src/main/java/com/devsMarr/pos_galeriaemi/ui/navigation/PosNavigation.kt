package com.devsMarr.pos_galeriaemi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.devsMarr.pos_galeriaemi.ui.presentation.admin_panel.AdminDashboardScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.category_form.CategoryFormScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.history.TicketHistoryScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.inventory.ProductListScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.pos.PosScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.product_form.ProductFormScreen

@Composable
fun PosNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Pos.route
    ) {

        // Pantalla Principal
        composable(route = Screen.Pos.route) {

            PosScreen(
            )
        }

        // Panel de Administración
        composable(route = Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onNavigateToDateReports = { navController.navigate(Screen.TicketHistory.route) },
                onNavigateToInventory = { navController.navigate(Screen.Inventory.route) },
                onNavigateToDailyReport = { /* TODO: Pendiente */ },
                onNavigateToEmployees = { /* TODO: Pendiente */ },
                onNavigateToSettings = { /* TODO: Pendiente */ },
                onBackClick = { navController.popBackStack() }
            )
        }

        // Historial de Tickets
        composable(route = Screen.TicketHistory.route) {
            TicketHistoryScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // Inventario
        composable(route = Screen.Inventory.route) {
            ProductListScreen(
                onNavigateToAddProduct = { navController.navigate(Screen.AddProduct.route) },
                onNavigateToEditProduct = { productId ->
                    navController.navigate(Screen.EditProduct.createRoute(productId))
                }
            )
        }

        // Agregar Categoría
        composable(route = Screen.AddCategory.route) {
            CategoryFormScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Agregar Producto
        composable(route = Screen.AddProduct.route) {
            ProductFormScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Editar Producto
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