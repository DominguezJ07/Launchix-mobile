package com.ec.launchix.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ec.launchix.ui.screens.*
import com.ec.launchix.ui.screens.profile.FavoritesScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    // ✅ ESTADO DE SESIÓN A NIVEL SUPERIOR - AQUÍ SE MANTIENE LA SESIÓN
    var isLoggedIn by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val currentRoute = currentDestination?.route

            // Solo mostrar bottom bar en pantallas principales
            val showBottomBar = when {
                currentRoute == Screen.Home.route -> true
                currentRoute == Screen.Products.route -> true
                currentRoute?.startsWith("products_category") == true -> true
                currentRoute == Screen.Services.route -> true
                currentRoute == Screen.Cart.route -> true
                currentRoute == Screen.Profile.route -> true
                else -> false
            }

            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    bottomNavItems.forEach { screen ->
                        val isSelected = when {
                            // Si estamos en una ruta de categoría y el screen es Home, marcar como seleccionado
                            screen.route == Screen.Home.route && currentRoute?.startsWith("products_category") == true -> true
                            // Selección normal para otras rutas
                            currentDestination?.hierarchy?.any { it.route == screen.route } == true -> true
                            else -> false
                        }

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    screen.icon,
                                    contentDescription = screen.title,
                                    tint = if (isSelected)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            label = {
                                Text(
                                    screen.title,
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                // Lógica especial para el botón de Home
                                if (screen.route == Screen.Home.route) {
                                    navController.navigate(Screen.Home.route) {
                                        // Limpiar todo el stack hasta llegar a Home
                                        popUpTo(Screen.Home.route) {
                                            inclusive = false
                                        }
                                        launchSingleTop = true
                                    }
                                } else {
                                    // Comportamiento normal para otras pestañas
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onProductClick = { productId ->
                        // TODO: Navegar a detalles del producto si lo necesitas
                    },
                    onServiceClick = { serviceId ->
                        // TODO: Navegar a detalles del servicio si lo necesitas
                    },
                    onCategoryClick = { categoryName ->
                        // ✅ Navegar a productos con categoría específica
                        navController.navigate("products_category/$categoryName")
                    },
                    onNavigateToProducts = {
                        // ✅ Navegar a todos los productos (pestaña normal)
                        navController.navigate(Screen.Products.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    // ✅ NUEVO: Navegar a la vista de servicios
                    onNavigateToServices = {
                        navController.navigate(Screen.Services.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            // Ruta normal de productos (pestaña del bottom nav)
            composable(Screen.Products.route) {
                ProductsScreen(
                    onProductClick = { productId ->
                        // TODO: Navegar a detalles del producto
                    }
                )
            }

            // ✅ NUEVA RUTA: Productos filtrados por categoría
            composable(
                route = "products_category/{category}",
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: "Todos"

                ProductsScreen(
                    initialCategory = category,
                    onProductClick = { productId ->
                        // TODO: Navegar a detalles del producto
                    }
                )
            }

            composable(Screen.Services.route) {
                ServicesScreen(
                    onServiceClick = { serviceId ->
                        // TODO: Navegar a detalles del servicio
                    }
                )
            }

            composable(Screen.Cart.route) {
                CartScreen()
            }

            // ✅ PERFIL CON ESTADO DE SESIÓN MANEJADO AQUÍ
            composable(Screen.Profile.route) {
                ProfileScreen(
                    navController = navController,
                    isLoggedIn = isLoggedIn, // ✅ Pasar el estado desde aquí
                    onLoginSuccess = {
                        isLoggedIn = true // ✅ Solo se activa al hacer login exitoso
                    },
                    onLogout = {
                        isLoggedIn = false // ✅ Solo se activa al presionar "Cerrar Sesión"
                    }
                )
            }

            // ✅ Todas estas pantallas ya están bien configuradas
            composable("orders") {
                OrdersScreen(navController = navController)
            }

            composable("favorites") {
                FavoritesScreen(navController = navController) // ✅ Ahora coincide
            }

            composable("settings") {
                SettingsScreen(navController = navController)
            }

            composable("help") {
                HelpScreen(navController = navController)
            }
        }
    }
}