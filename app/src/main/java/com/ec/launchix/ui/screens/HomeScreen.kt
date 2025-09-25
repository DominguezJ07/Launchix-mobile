package com.ec.launchix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ec.launchix.data.SampleData
import com.ec.launchix.ui.theme.LaunchixTheme

// Clases de datos para notificaciones
data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val type: NotificationType,
    val isRead: Boolean
)

enum class NotificationType {
    OFFER, ORDER, SERVICE, PAYMENT, GENERAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProductClick: (String) -> Unit = {},
    onServiceClick: (String) -> Unit = {},
    onCategoryClick: (String) -> Unit = {},
    onNavigateToProducts: () -> Unit = {},
    onNavigateToServices: () -> Unit = {} // ✅ NUEVO: Parámetro para navegar a servicios
) {
    // Estados para el modal de detalles de productos
    var showProductDetail by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<com.ec.launchix.data.Product?>(null) }
    var favoriteProducts by remember { mutableStateOf(setOf<String>()) }

    // Estados para el modal de detalles de servicios
    var showServiceDetail by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf<com.ec.launchix.data.Service?>(null) }

    // Estado para mostrar todas las categorías
    var showAllCategories by remember { mutableStateOf(false) }

    // Estados para notificaciones
    var showNotifications by remember { mutableStateOf(false) }
    var notificationCount by remember { mutableStateOf(3) }

    // Estados para búsqueda
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchHistory by remember { mutableStateOf(listOf("iPhone", "Reparación", "AirPods")) }

    // Filtrar productos y servicios basado en la búsqueda
    val filteredProducts = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            SampleData.sampleProducts
        } else {
            SampleData.sampleProducts.filter { product ->
                product.name.contains(searchQuery, ignoreCase = true) ||
                        product.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val filteredServices = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            SampleData.sampleServices
        } else {
            SampleData.sampleServices.filter { service ->
                service.name.contains(searchQuery, ignoreCase = true) ||
                        service.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Sugerencias de búsqueda
    val searchSuggestions = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            listOf("iPhone 14", "Reparación pantalla", "AirPods Pro", "MacBook", "Samsung Galaxy", "Xiaomi")
        } else {
            val productSuggestions = SampleData.sampleProducts
                .filter { it.name.contains(searchQuery, ignoreCase = true) }
                .map { it.name }
            val serviceSuggestions = SampleData.sampleServices
                .filter { it.name.contains(searchQuery, ignoreCase = true) }
                .map { it.name }
            (productSuggestions + serviceSuggestions).distinct().take(6)
        }
    }

    // Datos de ejemplo para notificaciones
    val sampleNotifications = remember {
        listOf(
            NotificationItem(
                id = "1",
                title = "¡Nueva oferta disponible!",
                message = "AirPods Pro 2 con 20% de descuento por tiempo limitado",
                time = "Hace 2 min",
                type = NotificationType.OFFER,
                isRead = false
            ),
            NotificationItem(
                id = "2",
                title = "Tu pedido ha sido enviado",
                message = "Tu iPhone 14 Pro está en camino. Llegará mañana entre 2-4 PM",
                time = "Hace 1 hora",
                type = NotificationType.ORDER,
                isRead = false
            ),
            NotificationItem(
                id = "3",
                title = "Servicio completado",
                message = "La reparación de tu MacBook ha sido completada exitosamente",
                time = "Hace 3 horas",
                type = NotificationType.SERVICE,
                isRead = true
            ),
            NotificationItem(
                id = "4",
                title = "Recordatorio de pago",
                message = "Tienes un pago pendiente por $150.00. Completa tu compra ahora",
                time = "Hace 1 día",
                type = NotificationType.PAYMENT,
                isRead = true
            )
        )
    }

    // Inicializar favoritos
    LaunchedEffect(Unit) {
        favoriteProducts = SampleData.sampleProducts
            .filter { it.isFavorite }
            .map { it.id }
            .toSet()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header con saludo y búsqueda
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "¡Hola! Bienvenido de vuelta",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Launchix",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row {
                        // Botón de notificaciones con badge
                        Box {
                            IconButton(
                                onClick = { showNotifications = true }
                            ) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "Notificaciones",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }

                            // Badge con número de notificaciones
                            if (notificationCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(
                                            MaterialTheme.colorScheme.error,
                                            CircleShape
                                        )
                                        .align(Alignment.TopEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (notificationCount > 99) "99+" else notificationCount.toString(),
                                        color = MaterialTheme.colorScheme.onError,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = { /* TODO: Carrito */ }
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Carrito",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Barra de búsqueda
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { newQuery ->
                        searchQuery = newQuery
                    },
                    onSearch = { query ->
                        // Agregar al historial si no está vacío y no existe
                        if (query.isNotBlank() && !searchHistory.contains(query)) {
                            searchHistory = listOf(query) + searchHistory.take(4)
                        }
                        isSearchActive = false
                    },
                    active = isSearchActive,
                    onActiveChange = { active ->
                        isSearchActive = active
                    },
                    placeholder = { Text("Buscar productos y servicios...") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Buscar"
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    searchQuery = ""
                                }
                            ) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Limpiar búsqueda"
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Contenido del SearchBar expandido
                    SearchContent(
                        searchQuery = searchQuery,
                        searchHistory = searchHistory,
                        searchSuggestions = searchSuggestions,
                        filteredProducts = filteredProducts,
                        filteredServices = filteredServices,
                        onQueryChange = { query ->
                            searchQuery = query
                        },
                        onSearchSubmit = { query ->
                            if (query.isNotBlank() && !searchHistory.contains(query)) {
                                searchHistory = listOf(query) + searchHistory.take(4)
                            }
                            isSearchActive = false
                        },
                        onProductClick = { product ->
                            selectedProduct = product
                            showProductDetail = true
                            isSearchActive = false
                        },
                        onServiceClick = { service ->
                            selectedService = service
                            showServiceDetail = true
                            isSearchActive = false
                        },
                        favoriteProducts = favoriteProducts,
                        onFavoriteToggle = { productId ->
                            favoriteProducts = if (favoriteProducts.contains(productId)) {
                                favoriteProducts - productId
                            } else {
                                favoriteProducts + productId
                            }
                        }
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Sección de categorías
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Categorías",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        TextButton(onClick = { showAllCategories = true }) {
                            Text("Ver todas")
                        }
                    }

                    if (showAllCategories) {
                        // Mostrar todas las categorías en grid
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            modifier = Modifier.height(
                                // Calcular altura dinámica basada en el número de categorías
                                ((SampleData.sampleCategories.size + 2) / 3 * 120).dp
                            )
                        ) {
                            items(SampleData.sampleCategories) { category ->
                                CategoryCard(
                                    category = category,
                                    onClick = {
                                        onCategoryClick(category.name)
                                    }
                                )
                            }
                        }

                        // Botón para colapsar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            TextButton(onClick = { showAllCategories = false }) {
                                Icon(
                                    Icons.Default.ExpandLess,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ver menos")
                            }
                        }
                    } else {
                        // Mostrar solo las primeras 6 categorías
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(15.dp),
                            contentPadding = PaddingValues(vertical = 7.dp)
                        ) {
                            items(SampleData.sampleCategories.take(6)) { category ->
                                CategoryCard(
                                    category = category,
                                    onClick = {
                                        onCategoryClick(category.name)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Sección Productos Destacados
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Productos Destacados",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        TextButton(onClick = onNavigateToProducts) {
                            Text("Ver todos")
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(SampleData.sampleProducts.take(5)) { product ->
                            ProductCard(
                                product = product,
                                isFavorite = favoriteProducts.contains(product.id),
                                onFavoriteToggle = { productId ->
                                    favoriteProducts = if (favoriteProducts.contains(productId)) {
                                        favoriteProducts - productId
                                    } else {
                                        favoriteProducts + productId
                                    }
                                },
                                onClick = {
                                    selectedProduct = product
                                    showProductDetail = true
                                }
                            )
                        }
                    }
                }
            }

            // Sección Servicios Destacados
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Servicios Destacados",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        // ✅ MODIFICADO: Usar onNavigateToServices en lugar del comentario
                        TextButton(onClick = onNavigateToServices) {
                            Text("Ver todos")
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(SampleData.sampleServices.take(5)) { service ->
                            ServiceCard(
                                service = service,
                                onClick = {
                                    selectedService = service
                                    showServiceDetail = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal de detalles del producto
    if (showProductDetail && selectedProduct != null) {
        ProductDetailModal(
            product = selectedProduct!!,
            isFavorite = favoriteProducts.contains(selectedProduct!!.id),
            onFavoriteToggle = { productId ->
                favoriteProducts = if (favoriteProducts.contains(productId)) {
                    favoriteProducts - productId
                } else {
                    favoriteProducts + productId
                }
            },
            onDismiss = { showProductDetail = false }
        )
    }

    // Modal de detalles del servicio
    if (showServiceDetail && selectedService != null) {
        ServiceDetailModal(
            service = selectedService!!,
            onDismiss = { showServiceDetail = false }
        )
    }

    // Modal de notificaciones
    if (showNotifications) {
        NotificationsModal(
            notifications = sampleNotifications,
            onDismiss = { showNotifications = false },
            onNotificationRead = { notificationId ->
                // Reducir el contador solo si la notificación no estaba leída
                val notification = sampleNotifications.find { it.id == notificationId }
                if (notification?.isRead == false) {
                    notificationCount = maxOf(0, notificationCount - 1)
                }
            },
            onMarkAllAsRead = {
                notificationCount = 0
            }
        )
    }
}

@Composable
fun CategoryCard(
    category: com.ec.launchix.data.Category,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = category.icon,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SearchContent(
    searchQuery: String,
    searchHistory: List<String>,
    searchSuggestions: List<String>,
    filteredProducts: List<com.ec.launchix.data.Product>,
    filteredServices: List<com.ec.launchix.data.Service>,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: (String) -> Unit,
    onProductClick: (com.ec.launchix.data.Product) -> Unit,
    onServiceClick: (com.ec.launchix.data.Service) -> Unit,
    favoriteProducts: Set<String>,
    onFavoriteToggle: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 500.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Historial de búsqueda
        if (searchQuery.isEmpty() && searchHistory.isNotEmpty()) {
            item {
                Text(
                    text = "Búsquedas recientes",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(searchHistory) { historyItem ->
                SearchHistoryItem(
                    text = historyItem,
                    onClick = {
                        onQueryChange(historyItem)
                        onSearchSubmit(historyItem)
                    }
                )
            }
        }

        // Sugerencias de búsqueda
        if (searchQuery.isNotEmpty() && searchSuggestions.isNotEmpty()) {
            item {
                Text(
                    text = "Sugerencias",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(searchSuggestions) { suggestion ->
                SearchSuggestionItem(
                    text = suggestion,
                    query = searchQuery,
                    onClick = {
                        onQueryChange(suggestion)
                        onSearchSubmit(suggestion)
                    }
                )
            }
        }

        // Resultados de productos
        if (searchQuery.isNotEmpty() && filteredProducts.isNotEmpty()) {
            item {
                Text(
                    text = "Productos (${filteredProducts.size})",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            items(filteredProducts.take(3)) { product ->
                SearchResultProductItem(
                    product = product,
                    isFavorite = favoriteProducts.contains(product.id),
                    onFavoriteToggle = onFavoriteToggle,
                    onClick = { onProductClick(product) },
                    searchQuery = searchQuery
                )
            }

            if (filteredProducts.size > 3) {
                item {
                    TextButton(
                        onClick = {
                            onSearchSubmit(searchQuery)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver todos los ${filteredProducts.size} productos")
                    }
                }
            }
        }

        // Resultados de servicios
        if (searchQuery.isNotEmpty() && filteredServices.isNotEmpty()) {
            item {
                Text(
                    text = "Servicios (${filteredServices.size})",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            items(filteredServices.take(3)) { service ->
                SearchResultServiceItem(
                    service = service,
                    onClick = { onServiceClick(service) },
                    searchQuery = searchQuery
                )
            }

            if (filteredServices.size > 3) {
                item {
                    TextButton(
                        onClick = {
                            onSearchSubmit(searchQuery)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver todos los ${filteredServices.size} servicios")
                    }
                }
            }
        }

        // Estado vacío
        if (searchQuery.isNotEmpty() && filteredProducts.isEmpty() && filteredServices.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "No se encontraron resultados",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        text = "Intenta con otros términos de búsqueda",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SearchHistoryItem(
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.History,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f),
            fontSize = 14.sp
        )
        Icon(
            Icons.Default.NorthWest,
            contentDescription = "Usar búsqueda",
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SearchSuggestionItem(
    text: String,
    query: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f),
            fontSize = 14.sp
        )
        Icon(
            Icons.Default.NorthWest,
            contentDescription = "Buscar",
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SearchResultProductItem(
    product: com.ec.launchix.data.Product,
    isFavorite: Boolean,
    onFavoriteToggle: (String) -> Unit,
    onClick: () -> Unit,
    searchQuery: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen placeholder
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ShoppingBag,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color(0xFFFFC107)
                    )
                    Text(
                        text = "${product.rating} (${product.reviewCount})",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Text(
                    text = "${product.price}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            IconButton(
                onClick = { onFavoriteToggle(product.id) }
            ) {
                Icon(
                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                    tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SearchResultServiceItem(
    service: com.ec.launchix.data.Service,
    onClick: () -> Unit,
    searchQuery: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen placeholder
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Build,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = service.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color(0xFFFFC107)
                    )
                    Text(
                        text = "${service.rating} (${service.reviewCount})",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Text(
                    text = "${service.price}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "Ver servicio",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ProductCard(
    product: com.ec.launchix.data.Product,
    isFavorite: Boolean = false,
    onFavoriteToggle: ((String) -> Unit)? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Imagen del producto (placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ShoppingBag,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )

                // Badge de oferta
                if (product.isOnSale) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(
                                MaterialTheme.colorScheme.error,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "OFERTA",
                            color = MaterialTheme.colorScheme.onError,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Botón de favorito
                onFavoriteToggle?.let { toggle ->
                    IconButton(
                        onClick = { toggle(product.id) },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                            tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFFFFC107)
                    )
                    Text(
                        text = "${product.rating}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = " (${product.reviewCount})",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${product.price}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    product.originalPrice?.let { originalPrice ->
                        if (originalPrice > product.price) {
                            Text(
                                text = "$${originalPrice}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceCard(
    service: com.ec.launchix.data.Service,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Imagen del servicio (placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.secondaryContainer,
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Build,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )

                // Badge de oferta
                if (service.isOnSale) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(
                                MaterialTheme.colorScheme.error,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "OFERTA",
                            color = MaterialTheme.colorScheme.onError,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = service.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFFFFC107)
                    )
                    Text(
                        text = "${service.rating}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = " (${service.reviewCount})",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (service.duration.isNotEmpty()) {
                    Text(
                        text = "Duración: ${service.duration}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${service.price}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    service.originalPrice?.let { originalPrice ->
                        if (originalPrice > service.price) {
                            Text(
                                text = "$${originalPrice}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceDetailModal(
    service: com.ec.launchix.data.Service,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = service.name,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFFFFC107)
                    )
                    Text(
                        text = "${service.rating} (${service.reviewCount} reseñas)",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    if (service.isOnSale) {
                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .background(Color(0xFF4CAF50), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Disponible",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Precio
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "$${service.price}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = " Precio fijo",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Duración y domicilio
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "1 - 2 horas",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, end = 16.dp)
                    )
                    Icon(
                        Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "A domicilio",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Proveedor
                Text(
                    text = "Proveedor del servicio",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "TP",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Column(
                        modifier = Modifier.padding(start = 12.dp)
                    ) {
                        Text(
                            text = "TechFix Pro",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "5 años de experiencia",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Servicios completados:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "1250",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Column {
                        Text(
                            text = "Tiempo de respuesta:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "30 minutos",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Servicio profesional de reparación de smartphones. Manejo amplia variedad de modelos: iPhone, Samsung, Xiaomi, cámaras, altavoces y más. Técnicos certificados con garantía de 6 meses en todas las reparaciones.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Reservar servicio",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        },
        dismissButton = null
    )
}

@Composable
fun NotificationsModal(
    notifications: List<NotificationItem>,
    onDismiss: () -> Unit,
    onNotificationRead: (String) -> Unit,
    onMarkAllAsRead: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.widthIn(max = 400.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notificaciones",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                TextButton(
                    onClick = onMarkAllAsRead
                ) {
                    Text(
                        text = "Marcar todas como leídas",
                        fontSize = 12.sp
                    )
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.height(400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notifications) { notification ->
                    NotificationCard(
                        notification = notification,
                        onClick = {
                            onNotificationRead(notification.id)
                        }
                    )
                }

                if (notifications.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.NotificationsNone,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "No tienes notificaciones",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun NotificationCard(
    notification: NotificationItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icono basado en el tipo de notificación
            val (icon, iconColor) = when (notification.type) {
                NotificationType.OFFER -> Icons.Default.LocalOffer to Color(0xFF4CAF50)
                NotificationType.ORDER -> Icons.Default.LocalShipping to Color(0xFF2196F3)
                NotificationType.SERVICE -> Icons.Default.Build to Color(0xFF9C27B0)
                NotificationType.PAYMENT -> Icons.Default.Payment to Color(0xFFFF9800)
                NotificationType.GENERAL -> Icons.Default.Info to Color(0xFF607D8B)
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = iconColor
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                )                                                                                                                                                                                   {
                    Text(
                        text = notification.title,
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.SemiBold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                .padding(start = 8.dp)
                        )
                    }
                }

                Text(
                    text = notification.message,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 4.dp),
                    
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = notification.time,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}