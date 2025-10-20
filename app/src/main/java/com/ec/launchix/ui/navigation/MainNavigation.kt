package com.ec.launchix.ui.navigation

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ec.launchix.ui.screens.*
import com.ec.launchix.ui.screens.profile.FavoritesScreen
import com.ec.launchix.ui.screens.auth.AuthPromptScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit
) {
    var isLoggedIn by rememberSaveable { mutableStateOf(false) }
    var userName by rememberSaveable { mutableStateOf("Usuario Launchix") }
    var userEmail by rememberSaveable { mutableStateOf("usuario@launchix.com") }
    var authToken by rememberSaveable { mutableStateOf<String?>(null) }
    var profileImageUriString by rememberSaveable { mutableStateOf<String?>(null) }
    var userPhone by rememberSaveable { mutableStateOf("+593 999 999 999") }

    LaunchedEffect(isLoggedIn) {
        Log.d("MainNavigation", "isLoggedIn cambió a: $isLoggedIn")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AppContent(
            isDarkMode = isDarkMode,
            onDarkModeToggle = onDarkModeToggle,
            userName = userName,
            userEmail = userEmail,
            userPhone = userPhone,
            profileImageUriString = profileImageUriString,
            isLoggedIn = isLoggedIn,
            onLogout = {
                isLoggedIn = false
                userName = "Usuario Launchix"
                userEmail = "usuario@launchix.com"
                authToken = null
                profileImageUriString = null
            },
            onLoginSuccess = { name, email, token ->
                Log.d("MainNavigation", "onLoginSuccess llamado - name: $name, email: $email")
                userName = name
                userEmail = email
                authToken = token
                isLoggedIn = true
                Log.d("MainNavigation", "isLoggedIn después de cambio: $isLoggedIn")
            },
            onUserInfoChange = { name, email, phone ->
                userName = name
                userEmail = email
                userPhone = phone
            },
            onProfileImageSelected = { uri ->
                profileImageUriString = uri?.toString()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppContent(
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    userName: String,
    userEmail: String,
    userPhone: String,
    profileImageUriString: String?,
    isLoggedIn: Boolean,
    onLogout: () -> Unit,
    onLoginSuccess: (String, String, String) -> Unit,
    onUserInfoChange: (String, String, String) -> Unit,
    onProfileImageSelected: (Uri?) -> Unit
) {
    val navController = rememberNavController()
    val cartViewModel: CartViewModel = viewModel()
    val profileImageUri = profileImageUriString?.let { Uri.parse(it) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val currentRoute = currentDestination?.route

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
                            screen.route == Screen.Home.route && currentRoute?.startsWith("products_category") == true -> true
                            currentDestination?.hierarchy?.any { it.route == screen.route } == true -> true
                            else -> false
                        }

                        NavigationBarItem(
                            icon = {
                                if (screen.route == Screen.Cart.route) {
                                    val totalItems = cartViewModel.getTotalItems()
                                    BadgedBox(
                                        badge = {
                                            if (totalItems > 0) {
                                                Badge(
                                                    containerColor = MaterialTheme.colorScheme.error,
                                                    contentColor = MaterialTheme.colorScheme.onError
                                                ) {
                                                    Text("$totalItems")
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            screen.icon,
                                            contentDescription = screen.title,
                                            tint = if (isSelected)
                                                MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                } else {
                                    Icon(
                                        screen.icon,
                                        contentDescription = screen.title,
                                        tint = if (isSelected)
                                            MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
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
                                if (screen.route == Screen.Home.route) {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Home.route) {
                                            inclusive = false
                                        }
                                        launchSingleTop = true
                                    }
                                } else {
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
                    onProductClick = { productId -> },
                    onServiceClick = { serviceId -> },
                    onCategoryClick = { categoryName ->
                        navController.navigate("products_category/$categoryName")
                    },
                    onNavigateToProducts = {
                        navController.navigate(Screen.Products.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
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

            composable(Screen.Products.route) {
                ProductsScreen(
                    cartViewModel = cartViewModel,
                    onProductClick = { productId -> },
                    onNavigateToCart = {
                        if (isLoggedIn) {
                            navController.navigate(Screen.Cart.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            navController.navigate("auth_checkout") {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }

            composable(
                route = "products_category/{category}",
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: "Todos"

                ProductsScreen(
                    cartViewModel = cartViewModel,
                    initialCategory = category,
                    onProductClick = { productId -> },
                    onNavigateToCart = {
                        if (isLoggedIn) {
                            navController.navigate(Screen.Cart.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            navController.navigate("auth_checkout") {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }

            composable(Screen.Services.route) {
                ServicesScreen(
                    onServiceClick = { serviceId -> }
                )
            }

            // ✅ MODIFICACIÓN PRINCIPAL: CartScreen ahora recibe isLoggedIn y onNavigateToAuth
            composable(Screen.Cart.route) {
                CartScreen(
                    cartViewModel = cartViewModel,
                    onNavigateToProducts = {
                        navController.navigate(Screen.Products.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    isLoggedIn = isLoggedIn, // ✅ Pasa el estado de login
                    onNavigateToAuth = { // ✅ Callback para ir a autenticación
                        navController.navigate("auth_checkout") {
                            launchSingleTop = true
                        }
                    }
                )
            }

            // ✅ RUTA DE AUTH: Para checkout o login desde cualquier parte
            composable("auth_checkout") {
                AuthPromptScreen(
                    onLoginSuccess = { name, email, token ->
                        onLoginSuccess(name, email, token)
                        // Después de login exitoso, lo lleva de vuelta al carrito
                        navController.navigate(Screen.Cart.route) {
                            popUpTo("auth_checkout") { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    navController = navController,
                    isLoggedIn = isLoggedIn,
                    onLoginClick = {
                        navController.navigate("auth_checkout")
                    },
                    onLogout = {
                        onLogout()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    profileImageUri = profileImageUri,
                    onProfileImageSelected = onProfileImageSelected,
                    userName = userName,
                    userEmail = userEmail
                )
            }

            composable("orders") {
                OrdersScreen(navController = navController)
            }

            composable("favorites") {
                FavoritesScreen(navController = navController)
            }

            composable("settings") {
                SettingsScreen(
                    navController = navController,
                    userName = userName,
                    userEmail = userEmail,
                    userPhone = userPhone,
                    onUserInfoChange = onUserInfoChange,
                    isDarkMode = isDarkMode,
                    onDarkModeToggle = onDarkModeToggle
                )
            }

            composable("help") {
                HelpScreen(navController = navController)
            }
        }
    }
}