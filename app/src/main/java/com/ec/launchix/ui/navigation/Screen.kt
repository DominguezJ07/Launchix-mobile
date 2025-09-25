package com.ec.launchix.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Inicio", Icons.Default.Home)
    object Products : Screen("products", "Productos", Icons.Default.ShoppingBag)
    object Services : Screen("services", "Servicios", Icons.Default.Build)
    object Cart : Screen("cart", "Carrito", Icons.Default.ShoppingCart)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Products,
    Screen.Services,
    Screen.Cart,
    Screen.Profile
)
