package com.devsMarr.pos_galeriaemi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.devsMarr.pos_galeriaemi.ui.presentation.user_form.UserFormScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.users.UserListScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.users.UserViewModel

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
                onNavigateToEmployees = { navController.navigate(Screen.Users.route) },
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

        // Lista de Usuarios
        composable(Screen.Users.route) {
            val viewModel: UserViewModel = hiltViewModel()
            val users by viewModel.users.collectAsState()

            UserListScreen(
                users = users,
                onNavigateToAddUser = { navController.navigate(Screen.AddUser.route) },
                onNavigateToEditUser = { userId -> navController.navigate(Screen.EditUser.createRoute(userId)) },
                onDeactivateUser = { user -> viewModel.deactivateUser(user) },
                onBackClick = { navController.popBackStack() }
            )
        }

        // Agregar Usuario
        composable(route = Screen.AddUser.route) {
            UserFormScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Editar Usuario
        composable(
            route = Screen.EditUser.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.LongType }
            )
        ) {
            UserFormScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}