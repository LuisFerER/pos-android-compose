package com.devsMarr.pos_galeriaemi.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.devsMarr.pos_galeriaemi.ui.presentation.admin_panel.AdminDashboardScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.category_form.CategoryFormScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.history.TicketHistoryScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.inventory.ProductListScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.login.LoginScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.pos.PosScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.product_form.ProductFormScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.user_form.UserFormScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.users.UserListScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.users.UserViewModel
import com.devsMarr.pos_galeriaemi.ui.presentation.pos.components.PosDrawer // <-- IMPORT DEL DRAWER
import com.devsMarr.pos_galeriaemi.ui.presentation.settings.SettingsScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.pos.components.PosDrawer
import com.devsMarr.pos_galeriaemi.ui.presentation.pos.components.CloseShiftDialog
import kotlinx.coroutines.launch

@Composable
fun PosNavigation(
    navViewModel: NavViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentUser by navViewModel.currentUser.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val gesturesEnabled = currentRoute != Screen.Login.route

    val navUiState by navViewModel.uiState.collectAsStateWithLifecycle()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        drawerContent = {
            if (currentUser != null && currentRoute != Screen.Login.route) {
                // NUESTRO DRAWER LIMPIO
                PosDrawer(
                    currentUser = currentUser!!,
                    currentRoute = currentRoute,
                    onNavigateToPos = {
                        navController.navigate(Screen.Pos.route) { popUpTo(Screen.Pos.route) { inclusive = true } }
                        scope.launch { drawerState.close() }
                    },
                    onNavigateToAdmin = {
                        navController.navigate(Screen.AdminDashboard.route) { popUpTo(Screen.Pos.route) }
                        scope.launch { drawerState.close() }
                    },
                    onLogoutClick = {
                        navViewModel.logout()
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Login.route) { popUpTo(0) }
                    },
                    onCloseShiftClick = {
                        navViewModel.onIntentCloseShift()
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        if (navUiState.showCloseShiftDialog) {
            CloseShiftDialog(
                expectedAmount = navUiState.expectedAmount,
                onConfirm = { actualAmount, notes ->
                    navViewModel.confirmCloseShift(actualAmount, notes) {
                        // Una vez que la caja se cierra en BD, mandamos al Login
                        navController.navigate(Screen.Login.route) { popUpTo(0) }
                    }
                },
                onDismiss = { navViewModel.hideCloseShiftDialog() }
            )
        }

        // --- NAVHOST: SOLO RUTAS LIMPIAS ---
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route
        ) {
            composable(route = Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Pos.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                    }
                )
            }

            composable(route = Screen.Pos.route) {
                PosScreen(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onLogoutClick = {
                        navViewModel.logout()
                        navController.navigate(Screen.Login.route) { popUpTo(0) }
                    }
                )
            }

            composable(route = Screen.AdminDashboard.route) {
                AdminDashboardScreen(
                    onNavigateToDateReports = { navController.navigate(Screen.TicketHistory.route) },
                    onNavigateToInventory = { navController.navigate(Screen.Inventory.route) },
                    onNavigateToDailyReport = { /* TODO */ },
                    onNavigateToEmployees = { navController.navigate(Screen.Users.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(route = Screen.TicketHistory.route) {
                TicketHistoryScreen(onBackClick = { navController.popBackStack() })
            }

            composable(route = Screen.Inventory.route) {
                ProductListScreen(
                    onNavigateToAddProduct = { navController.navigate(Screen.AddProduct.route) },
                    onNavigateToEditProduct = { productId -> navController.navigate(Screen.EditProduct.createRoute(productId)) }
                )
            }

            composable(route = Screen.AddCategory.route) {
                CategoryFormScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(route = Screen.AddProduct.route) {
                ProductFormScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(
                route = Screen.EditProduct.route,
                arguments = listOf(navArgument("productId") { type = NavType.LongType })
            ) {
                ProductFormScreen(onNavigateBack = { navController.popBackStack() })
            }

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

            composable(route = Screen.AddUser.route) {
                UserFormScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(
                route = Screen.EditUser.route,
                arguments = listOf(navArgument("userId") { type = NavType.LongType })
            ) {
                UserFormScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(route = Screen.Settings.route) {
                SettingsScreen(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}