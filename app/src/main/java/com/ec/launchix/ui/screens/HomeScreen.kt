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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ec.launchix.data.SampleData
import com.ec.launchix.ui.theme.LaunchixTheme

// Data classes
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
    onNavigateToServices: () -> Unit = {}
) {
    // Estados consolidados
    var showProductDetail by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<com.ec.launchix.data.Product?>(null) }
    var showServiceDetail by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf<com.ec.launchix.data.Service?>(null) }
    var favoriteProducts by remember { mutableStateOf(setOf<String>()) }
    var showAllCategories by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }
    var notificationCount by remember { mutableStateOf(3) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchHistory by remember { mutableStateOf(listOf("iPhone", "Reparación", "AirPods")) }

    // Filtros y sugerencias
    val filteredProducts = remember(searchQuery) {
        if (searchQuery.isBlank()) SampleData.sampleProducts
        else SampleData.sampleProducts.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }
    }

    val filteredServices = remember(searchQuery) {
        if (searchQuery.isBlank()) SampleData.sampleServices
        else SampleData.sampleServices.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }
    }

    val searchSuggestions = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            listOf("iPhone 14", "Reparación pantalla", "AirPods Pro", "MacBook", "Samsung Galaxy", "Xiaomi")
        } else {
            (filteredProducts.map { it.name } + filteredServices.map { it.name }).distinct().take(6)
        }
    }

    // Datos de notificaciones
    val sampleNotifications = remember {
        listOf(
            NotificationItem("1", "¡Nueva oferta disponible!", "AirPods Pro 2 con 20% de descuento por tiempo limitado",
                "Hace 2 min", NotificationType.OFFER, false),
            NotificationItem("2", "Tu pedido ha sido enviado", "Tu iPhone 14 Pro está en camino. Llegará mañana entre 2-4 PM",
                "Hace 1 hora", NotificationType.ORDER, false),
            NotificationItem("3", "Servicio completado", "La reparación de tu MacBook ha sido completada exitosamente",
                "Hace 3 horas", NotificationType.SERVICE, true),
            NotificationItem("4", "Recordatorio de pago", "Tienes un pago pendiente por $150.00. Completa tu compra ahora",
                "Hace 1 día", NotificationType.PAYMENT, true)
        )
    }

    LaunchedEffect(Unit) {
        favoriteProducts = SampleData.sampleProducts.filter { it.isFavorite }.map { it.id }.toSet()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        HeaderSection(
            notificationCount = notificationCount,
            searchQuery = searchQuery,
            isSearchActive = isSearchActive,
            searchHistory = searchHistory,
            searchSuggestions = searchSuggestions,
            filteredProducts = filteredProducts,
            filteredServices = filteredServices,
            favoriteProducts = favoriteProducts,
            onNotificationsClick = { showNotifications = true },
            onQueryChange = { searchQuery = it },
            onSearchSubmit = { query ->
                if (query.isNotBlank() && !searchHistory.contains(query)) {
                    searchHistory = listOf(query) + searchHistory.take(4)
                }
                isSearchActive = false
            },
            onActiveChange = { isSearchActive = it },
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
            onFavoriteToggle = { productId ->
                favoriteProducts = if (favoriteProducts.contains(productId)) {
                    favoriteProducts - productId
                } else favoriteProducts + productId
            }
        )

        // Contenido principal
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Categorías
            item {
                CategoriesSection(
                    showAll = showAllCategories,
                    onToggleShowAll = { showAllCategories = !showAllCategories },
                    onCategoryClick = onCategoryClick
                )
            }

            // Productos destacados
            item {
                SectionHeader(title = "Productos Destacados", onSeeAllClick = onNavigateToProducts)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(SampleData.sampleProducts.take(5)) { product ->
                        ProductCard(
                            product = product,
                            isFavorite = favoriteProducts.contains(product.id),
                            onFavoriteToggle = { id ->
                                favoriteProducts = if (favoriteProducts.contains(id)) {
                                    favoriteProducts - id
                                } else favoriteProducts + id
                            },
                            onClick = {
                                selectedProduct = product
                                showProductDetail = true
                            }
                        )
                    }
                }
            }

            // Servicios destacados
            item {
                SectionHeader(title = "Servicios Destacados", onSeeAllClick = onNavigateToServices)
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

    // Modales
    if (showProductDetail && selectedProduct != null) {
        ProductDetailModal(
            product = selectedProduct!!,
            isFavorite = favoriteProducts.contains(selectedProduct!!.id),
            onFavoriteToggle = { id ->
                favoriteProducts = if (favoriteProducts.contains(id)) {
                    favoriteProducts - id
                } else favoriteProducts + id
            },
            onDismiss = { showProductDetail = false }
        )
    }

    if (showServiceDetail && selectedService != null) {
        ServiceDetailModal(
            service = selectedService!!,
            onDismiss = { showServiceDetail = false }
        )
    }

    if (showNotifications) {
        NotificationsModal(
            notifications = sampleNotifications,
            onDismiss = { showNotifications = false },
            onNotificationRead = { id ->
                val notification = sampleNotifications.find { it.id == id }
                if (notification?.isRead == false) {
                    notificationCount = maxOf(0, notificationCount - 1)
                }
            },
            onMarkAllAsRead = { notificationCount = 0 }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeaderSection(
    notificationCount: Int,
    searchQuery: String,
    isSearchActive: Boolean,
    searchHistory: List<String>,
    searchSuggestions: List<String>,
    filteredProducts: List<com.ec.launchix.data.Product>,
    filteredServices: List<com.ec.launchix.data.Service>,
    favoriteProducts: Set<String>,
    onNotificationsClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    onProductClick: (com.ec.launchix.data.Product) -> Unit,
    onServiceClick: (com.ec.launchix.data.Service) -> Unit,
    onFavoriteToggle: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("¡Hola! Bienvenido de vuelta", color = MaterialTheme.colorScheme.onPrimary, fontSize = 14.sp)
                    Text("Launchix", color = MaterialTheme.colorScheme.onPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }

                Row {
                    Box {
                        IconButton(onClick = onNotificationsClick) {
                            Icon(Icons.Default.Notifications, "Notificaciones", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                        if (notificationCount > 0) {
                            Badge(
                                count = notificationCount,
                                modifier = Modifier.align(Alignment.TopEnd)
                            )
                        }
                    }
                    IconButton(onClick = { /* Carrito */ }) {
                        Icon(Icons.Default.ShoppingCart, "Carrito", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SearchBar(
                query = searchQuery,
                onQueryChange = onQueryChange,
                onSearch = onSearchSubmit,
                active = isSearchActive,
                onActiveChange = onActiveChange,
                placeholder = { Text("Buscar productos y servicios...") },
                leadingIcon = { Icon(Icons.Default.Search, "Buscar") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Default.Clear, "Limpiar búsqueda")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                SearchContent(
                    searchQuery = searchQuery,
                    searchHistory = searchHistory,
                    searchSuggestions = searchSuggestions,
                    filteredProducts = filteredProducts,
                    filteredServices = filteredServices,
                    favoriteProducts = favoriteProducts,
                    onQueryChange = onQueryChange,
                    onSearchSubmit = onSearchSubmit,
                    onProductClick = onProductClick,
                    onServiceClick = onServiceClick,
                    onFavoriteToggle = onFavoriteToggle
                )
            }
        }
    }
}

@Composable
private fun Badge(count: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(20.dp)
            .background(MaterialTheme.colorScheme.error, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (count > 99) "99+" else count.toString(),
            color = MaterialTheme.colorScheme.onError,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CategoriesSection(
    showAll: Boolean,
    onToggleShowAll: () -> Unit,
    onCategoryClick: (String) -> Unit
) {
    Column {
        SectionHeader(title = "Categorías", onSeeAllClick = { onToggleShowAll() })

        if (showAll) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                modifier = Modifier.height(((SampleData.sampleCategories.size + 2) / 3 * 120).dp)
            ) {
                items(SampleData.sampleCategories) { category ->
                    CategoryCard(category = category, onClick = { onCategoryClick(category.name) })
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = { onToggleShowAll() }) {
                    Icon(Icons.Default.ExpandLess, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ver menos")
                }
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                contentPadding = PaddingValues(vertical = 7.dp)
            ) {
                items(SampleData.sampleCategories.take(6)) { category ->
                    CategoryCard(category = category, onClick = { onCategoryClick(category.name) })
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, onSeeAllClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        TextButton(onClick = onSeeAllClick) { Text("Ver todos") }
    }
}

@Composable
fun CategoryCard(category: com.ec.launchix.data.Category, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(100.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(category.icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(category.name, fontSize = 12.sp, fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
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
    favoriteProducts: Set<String>,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: (String) -> Unit,
    onProductClick: (com.ec.launchix.data.Product) -> Unit,
    onServiceClick: (com.ec.launchix.data.Service) -> Unit,
    onFavoriteToggle: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Historial
        if (searchQuery.isEmpty() && searchHistory.isNotEmpty()) {
            item { Text("Búsquedas recientes", fontWeight = FontWeight.SemiBold, fontSize = 16.sp) }
            items(searchHistory) { item ->
                SearchListItem(text = item, icon = Icons.Default.History, onClick = {
                    onQueryChange(item)
                    onSearchSubmit(item)
                })
            }
        }

        // Sugerencias
        if (searchQuery.isNotEmpty() && searchSuggestions.isNotEmpty()) {
            item { Text("Sugerencias", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp)) }
            items(searchSuggestions) { suggestion ->
                SearchListItem(text = suggestion, icon = Icons.Default.Search, onClick = {
                    onQueryChange(suggestion)
                    onSearchSubmit(suggestion)
                })
            }
        }

        // Resultados de productos
        if (searchQuery.isNotEmpty() && filteredProducts.isNotEmpty()) {
            item { Text("Productos (${filteredProducts.size})", fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp, modifier = Modifier.padding(top = 16.dp)) }
            items(filteredProducts.take(3)) { product ->
                SearchResultProductItem(product, favoriteProducts.contains(product.id),
                    onFavoriteToggle, { onProductClick(product) })
            }
            if (filteredProducts.size > 3) {
                item {
                    TextButton(onClick = { onSearchSubmit(searchQuery) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Ver todos los ${filteredProducts.size} productos")
                    }
                }
            }
        }

        // Resultados de servicios
        if (searchQuery.isNotEmpty() && filteredServices.isNotEmpty()) {
            item { Text("Servicios (${filteredServices.size})", fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp, modifier = Modifier.padding(top = 16.dp)) }
            items(filteredServices.take(3)) { service ->
                SearchResultServiceItem(service, { onServiceClick(service) })
            }
            if (filteredServices.size > 3) {
                item {
                    TextButton(onClick = { onSearchSubmit(searchQuery) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Ver todos los ${filteredServices.size} servicios")
                    }
                }
            }
        }

        // Estado vacío
        if (searchQuery.isNotEmpty() && filteredProducts.isEmpty() && filteredServices.isEmpty()) {
            item {
                EmptySearchState()
            }
        }
    }
}

@Composable
private fun SearchListItem(text: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text, modifier = Modifier.padding(start = 16.dp).weight(1f), fontSize = 14.sp)
        Icon(Icons.Default.NorthWest, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EmptySearchState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.SearchOff, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("No se encontraron resultados", fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 16.dp))
        Text("Intenta con otros términos de búsqueda", fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun SearchResultProductItem(
    product: com.ec.launchix.data.Product,
    isFavorite: Boolean,
    onFavoriteToggle: (String) -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Imagen del producto
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(product.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.Star, null, modifier = Modifier.size(12.dp), tint = Color(0xFFFFC107))
                    Text("${product.rating} (${product.reviewCount})", fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp))
                }
                Text("$${product.price}", fontWeight = FontWeight.Bold, fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 4.dp))
            }

            IconButton(onClick = { onFavoriteToggle(product.id) }) {
                Icon(
                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                    tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SearchResultServiceItem(service: com.ec.launchix.data.Service, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Imagen del servicio
            AsyncImage(
                model = service.imageUrl,
                contentDescription = service.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(service.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.Star, null, modifier = Modifier.size(12.dp), tint = Color(0xFFFFC107))
                    Text("${service.rating} (${service.reviewCount})", fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp))
                }
                Text("$${service.price}", fontWeight = FontWeight.Bold, fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 4.dp))
            }

            Icon(Icons.Default.ArrowForward, "Ver servicio", tint = MaterialTheme.colorScheme.onSurfaceVariant)
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
        modifier = Modifier.width(180.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp)
            ) {
                // Imagen del producto
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (product.isOnSale) {
                    Box(
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                            .background(MaterialTheme.colorScheme.error, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("OFERTA", color = MaterialTheme.colorScheme.onError, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                onFavoriteToggle?.let { toggle ->
                    IconButton(
                        onClick = { toggle(product.id) },
                        modifier = Modifier.align(Alignment.TopStart).padding(4.dp)
                            .background(Color.White.copy(alpha = 0.8f), CircleShape)
                    ) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                            tint = if (isFavorite) Color.Red else Color.Gray
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(product.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFC107))
                    Text("${product.rating} (${product.reviewCount})", fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("$${product.price}", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary)
                    product.originalPrice?.let { original ->
                        if (original > product.price) {
                            Text(
                                "$${original}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp),
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceCard(service: com.ec.launchix.data.Service, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(180.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp)
            ) {
                // Imagen del servicio
                AsyncImage(
                    model = service.imageUrl,
                    contentDescription = service.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (service.isOnSale) {
                    Box(
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                            .background(MaterialTheme.colorScheme.error, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("OFERTA", color = MaterialTheme.colorScheme.onError, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(service.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFC107))
                    Text("${service.rating} (${service.reviewCount})", fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (service.duration.isNotEmpty()) {
                    Text("Duración: ${service.duration}", fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${service.price}", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary)
                    service.originalPrice?.let { original ->
                        if (original > service.price) {
                            Text(
                                "${original}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp),
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductDetailModal(
    product: com.ec.launchix.data.Product,
    isFavorite: Boolean,
    onFavoriteToggle: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(product.name, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                IconButton(onClick = { onFavoriteToggle(product.id) }) {
                    Icon(
                        if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                        tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column {
                // Imagen del producto
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                    Icon(Icons.Default.Star, null, modifier = Modifier.size(16.dp), tint = Color(0xFFFFC107))
                    Text("${product.rating} (${product.reviewCount} reseñas)", fontSize = 14.sp, modifier = Modifier.padding(start = 4.dp))
                    if (product.isOnSale) {
                        Box(
                            modifier = Modifier.padding(start = 8.dp)
                                .background(MaterialTheme.colorScheme.error, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("OFERTA", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                    Text("${product.price}", fontSize = 24.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary)
                    product.originalPrice?.let { original ->
                        if (original > product.price) {
                            Text(
                                "${original}",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 8.dp),
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }
                }

                Text("Categoría: ${product.category}", fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp))

                Text("Descripción", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    product.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (product.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        product.tags.take(3).forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(tag, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(16.dp))
                Text("Agregar al carrito", modifier = Modifier.padding(start = 8.dp))
            }
        }
    )
}

@Composable
fun ServiceDetailModal(service: com.ec.launchix.data.Service, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(service.name, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                // Imagen del servicio
                AsyncImage(
                    model = service.imageUrl,
                    contentDescription = service.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                    Icon(Icons.Default.Star, null, modifier = Modifier.size(16.dp), tint = Color(0xFFFFC107))
                    Text("${service.rating} (${service.reviewCount} reseñas)", fontSize = 14.sp, modifier = Modifier.padding(start = 4.dp))
                    if (service.isOnSale) {
                        Box(
                            modifier = Modifier.padding(start = 8.dp)
                                .background(Color(0xFF4CAF50), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Disponible", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                    Text("${service.price}", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary)
                    service.originalPrice?.let { original ->
                        if (original > service.price) {
                            Text(
                                "${original}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 8.dp),
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                    Icon(Icons.Default.AccessTime, null, modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(service.duration, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp, end = 16.dp))
                    Icon(Icons.Default.Home, null, modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("A domicilio", fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                }

                Text("Descripción", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    service.description,
                    fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (service.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        service.tags.take(3).forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(tag, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(16.dp))
                Text("Reservar servicio", modifier = Modifier.padding(start = 8.dp))
            }
        }
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
                Text("Notificaciones", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                TextButton(onClick = onMarkAllAsRead) {
                    Text("Marcar todas como leídas", fontSize = 12.sp)
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.height(400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notifications) { notification ->
                    NotificationCard(notification = notification, onClick = { onNotificationRead(notification.id) })
                }

                if (notifications.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.NotificationsNone, null, modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("No tienes notificaciones", color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 16.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}

@Composable
fun NotificationCard(notification: NotificationItem, onClick: () -> Unit) {
    val (icon, iconColor) = when (notification.type) {
        NotificationType.OFFER -> Icons.Default.LocalOffer to Color(0xFF4CAF50)
        NotificationType.ORDER -> Icons.Default.LocalShipping to Color(0xFF2196F3)
        NotificationType.SERVICE -> Icons.Default.Build to Color(0xFF9C27B0)
        NotificationType.PAYMENT -> Icons.Default.Payment to Color(0xFFFF9800)
        NotificationType.GENERAL -> Icons.Default.Info to Color(0xFF607D8B)
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier.size(40.dp).background(iconColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, modifier = Modifier.size(20.dp), tint = iconColor)
            }

            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        notification.title,
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.SemiBold,
                        fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, CircleShape)
                                .padding(start = 8.dp)
                        )
                    }
                }

                Text(
                    notification.message, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2, modifier = Modifier.padding(top = 4.dp), overflow = TextOverflow.Ellipsis
                )

                Text(
                    notification.time, fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}