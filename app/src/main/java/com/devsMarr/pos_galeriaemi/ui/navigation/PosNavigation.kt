package com.devsMarr.pos_galeriaemi.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.devsMarr.pos_galeriaemi.domain.model.UserRole
import com.devsMarr.pos_galeriaemi.ui.presentation.admin_panel.AdminDashboardScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.category_form.CategoryFormScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.history.TicketHistoryScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.inventory.ProductListScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.login.LoginScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.pos.PosScreen
import com.devsMarr.pos_galeriaemi.ui.presentation.product_form.ProductFormScreen
import kotlinx.coroutines.launch
import androidx.compose.ui.text.style.TextAlign

@Composable
fun PosNavigation(
    navViewModel: NavViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    // Controles para el Menú Lateral
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Obtenemos al usuario activo desde el ViewModel
    val currentUser by navViewModel.currentUser.collectAsStateWithLifecycle()

    // Obtenemos la ruta actual para saber qué botón del menú está seleccionado
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Desactivamos el gesto de deslizar el menú si estamos en la pantalla de Login
    val gesturesEnabled = currentRoute != Screen.Login.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        drawerContent = {
            // Solo dibujamos el contenido del menú si hay un usuario logeado y no estamos en Login
            if (currentUser != null && currentRoute != Screen.Login.route) {
                ModalDrawerSheet(modifier = Modifier.width(300.dp)) {

                    // --- CABECERA DEL MENÚ ---
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                            // Aumentamos un poco el padding superior para que se vea mejor con la barra de estado
                            .padding(top = 48.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally // <-- AQUÍ LOGRAMOS EL CENTRADO
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "User",
                            tint = Color.White,
                            modifier = Modifier.size(80.dp) // Lo hice un poquito más grande
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = currentUser!!.fullName,
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center // Centra el texto si ocupa 2 líneas
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (currentUser!!.role == UserRole.ADMIN) "Administrador" else "Cajero",
                            color = MaterialTheme.colorScheme.primaryContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- BOTONES PARA TODOS LOS ROLES ---
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.PointOfSale, contentDescription = null) },
                        label = { Text("Punto de Venta") },
                        selected = currentRoute == Screen.Pos.route,
                        onClick = {
                            navController.navigate(Screen.Pos.route) {
                                popUpTo(Screen.Pos.route) { inclusive = true }
                            }
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )

                    // --- BOTONES EXCLUSIVOS DE ADMINISTRADOR ---
                    if (currentUser!!.role == UserRole.ADMIN) {
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                            label = { Text("Panel de Administración") },
                            selected = currentRoute == Screen.AdminDashboard.route,
                            onClick = {
                                navController.navigate(Screen.AdminDashboard.route) {
                                    popUpTo(Screen.Pos.route) // Mantiene la pantalla POS como la base de la pila
                                }
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f)) // Empuja el botón de salir hacia el fondo
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    // --- BOTÓN DE CERRAR SESIÓN ---
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                        label = { Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error) },
                        selected = false,
                        onClick = {
                            navViewModel.logout()
                            scope.launch { drawerState.close() }
                            // Borramos todo el historial y regresamos al Login
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0)
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)
                    )
                }
            }
        }
    ) {
        // --- NAVHOST ACTUAL ---
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route // INICIAMOS EN EL LOGIN
        ) {

            // RUTA DEL LOGIN
            composable(route = Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        // Al entrar, vamos al POS y borramos la pantalla de Login del historial
                        navController.navigate(Screen.Pos.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // RUTA DEL PUNTO DE VENTA
            composable(route = Screen.Pos.route) {
                PosScreen(
                    onOpenDrawer = {
                        scope.launch { drawerState.open() }
                    },
                    onLogoutClick = {
                        // Cerramos sesión y mandamos al Login
                        navViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0)
                        }
                    }
                )
            }

            // RUTA DEL PANEL DE ADMINISTRACIÓN
            composable(route = Screen.AdminDashboard.route) {
                AdminDashboardScreen(
                    onNavigateToDateReports = { navController.navigate(Screen.TicketHistory.route) },
                    onNavigateToInventory = { navController.navigate(Screen.Inventory.route) },
                    onNavigateToDailyReport = { /* TODO */ },
                    onNavigateToEmployees = { /* TODO */ },
                    onNavigateToSettings = { /* TODO */ },
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
        }
    }
}