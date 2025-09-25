package com.ec.launchix.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ec.launchix.data.SampleData
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    onProductClick: (String) -> Unit = {},
    initialCategory: String? = null
) {
    var selectedCategory by remember(initialCategory) {
        mutableStateOf(initialCategory ?: "Todos")
    }
    var showProductDetail by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<com.ec.launchix.data.Product?>(null) }

    // Estados para la búsqueda
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    // Estado para manejar los favoritos
    var favoriteProducts by remember { mutableStateOf(setOf<String>()) }

    // Inicializar favoritos con los productos que ya vienen marcados como favoritos
    LaunchedEffect(Unit) {
        favoriteProducts = SampleData.sampleProducts
            .filter { it.isFavorite }
            .map { it.id }
            .toSet()
    }

    // Efecto para actualizar la categoría cuando cambie initialCategory
    LaunchedEffect(initialCategory) {
        if (initialCategory != null) {
            selectedCategory = initialCategory
        }
    }

    // Filtrar productos según la categoría y búsqueda
    val filteredProducts = remember(selectedCategory, searchQuery) {
        var products = if (selectedCategory == "Todos") {
            SampleData.sampleProducts
        } else {
            SampleData.sampleProducts.filter { product ->
                product.category == selectedCategory
            }
        }

        // Aplicar filtro de búsqueda si hay texto
        if (searchQuery.isNotBlank()) {
            val queryLower = searchQuery.lowercase(Locale.getDefault())
            products = products.filter { product ->
                product.name.lowercase(Locale.getDefault()).contains(queryLower) ||
                        product.description.lowercase(Locale.getDefault()).contains(queryLower)
            }
        }

        products
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header con diseño más elegante y limpio
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF42A5F5), // Azul más suave
                                Color(0xFF1E88E5)  // Azul más profundo
                            )
                        )
                    )
            ) {
                // Elementos decorativos de fondo
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .offset(x = (-40).dp, y = (-40).dp)
                        .background(
                            Color.White.copy(alpha = 0.08f),
                            RoundedCornerShape(75.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .offset(x = 300.dp, y = 20.dp)
                        .background(
                            Color.White.copy(alpha = 0.05f),
                            RoundedCornerShape(50.dp)
                        )
                )

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Productos",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.5).sp
                            )
                            if (selectedCategory != "Todos") {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .background(
                                                Color.White.copy(alpha = 0.8f),
                                                RoundedCornerShape(2.dp)
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = selectedCategory,
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            // Mostrar resultados de búsqueda de forma más elegante
                            if (searchQuery.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                Color.White.copy(alpha = 0.2f),
                                                RoundedCornerShape(20.dp)
                                            )
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Search,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${filteredProducts.size} resultados",
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.15f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { /* Filtros */ },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.FilterList,
                                    contentDescription = "Filtros",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.15f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { /* Ordenar */ },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Sort,
                                    contentDescription = "Ordenar",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Barra de búsqueda con el diseño original pero mejorada visualmente
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = {
                            isSearchActive = false
                        },
                        active = isSearchActive,
                        onActiveChange = { isSearchActive = it },
                        placeholder = {
                            Text(
                                "Buscar productos...",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        searchQuery = ""
                                        isSearchActive = false
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Limpiar búsqueda",
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        // Sugerencias de búsqueda mientras se escribe
                        if (searchQuery.isNotBlank()) {
                            val suggestions = SampleData.sampleProducts
                                .filter {
                                    it.name.lowercase(Locale.getDefault())
                                        .contains(searchQuery.lowercase(Locale.getDefault()))
                                }
                                .take(5) // Mostrar máximo 5 sugerencias

                            LazyColumn {
                                items(suggestions) { product ->
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                text = product.name,
                                                fontSize = 14.sp
                                            )
                                        },
                                        supportingContent = {
                                            Text(
                                                text = product.description,
                                                fontSize = 12.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        },
                                        leadingContent = {
                                            Icon(
                                                Icons.Default.Search,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp),
                                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        },
                                        modifier = Modifier.clickable {
                                            searchQuery = product.name
                                            isSearchActive = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Filtros por categoría (ocultar si hay búsqueda activa)
            if (searchQuery.isBlank()) {
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                onClick = { selectedCategory = "Todos" },
                                label = { Text("Todos") },
                                selected = selectedCategory == "Todos",
                                leadingIcon = if (selectedCategory == "Todos") {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                } else null
                            )
                        }
                        items(SampleData.sampleCategories) { category ->
                            FilterChip(
                                onClick = { selectedCategory = category.name },
                                label = { Text(category.name) },
                                selected = selectedCategory == category.name,
                                leadingIcon = if (selectedCategory == category.name) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                } else null
                            )
                        }
                    }
                }
            }

            // Productos filtrados con estado vacío mejorado
            if (filteredProducts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        RoundedCornerShape(50.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (searchQuery.isNotBlank()) Icons.Default.SearchOff else Icons.Default.Inventory2,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = if (searchQuery.isNotBlank()) {
                                    "Sin resultados"
                                } else {
                                    "Categoría vacía"
                                },
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (searchQuery.isNotBlank()) {
                                    "No encontramos productos para \"$searchQuery\". Intenta con otros términos."
                                } else {
                                    "No hay productos disponibles en esta categoría."
                                },
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                            if (searchQuery.isNotBlank()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        searchQuery = ""
                                        selectedCategory = "Todos"
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Ver todos los productos")
                                }
                            }
                        }
                    }
                }
            } else {
                // Grid de productos filtrados
                items(filteredProducts.chunked(2)) { productsRow ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        productsRow.forEach { product ->
                            Box(modifier = Modifier.weight(1f)) {
                                ProductGridCard(
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
                        // Completar la fila si tiene solo un elemento
                        if (productsRow.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
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
}

@Composable
fun ProductGridCard(
    product: com.ec.launchix.data.Product,
    isFavorite: Boolean,
    onFavoriteToggle: (String) -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Imagen del producto con imagen por defecto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                // Usar gradiente con ícono por defecto en lugar de imágenes externas
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                            ),
                            RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

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
                IconButton(
                    onClick = { onFavoriteToggle(product.id) },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(
                            Color.White.copy(alpha = 0.9f),
                            CircleShape
                        )
                        .size(32.dp)
                ) {
                    Icon(
                        if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                        tint = if (isFavorite) Color.Red else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
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

                Text(
                    text = product.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
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
                                        modifier = Modifier.padding(start = 4.dp),
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                }
                            }
                        }
                    }

                    IconButton(
                        onClick = { /* Agregar al carrito */ },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.AddShoppingCart,
                            contentDescription = "Agregar al carrito",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailModal(
    product: com.ec.launchix.data.Product,
    isFavorite: Boolean,
    onFavoriteToggle: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var quantity by remember { mutableStateOf(1) }

    val tabs = listOf("Descripción", "Características", "Especificaciones", "Reseñas")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                // Header con botón de cerrar
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Cerrar"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Compartir */ }) {
                            Icon(Icons.Default.Share, contentDescription = "Compartir")
                        }
                        // Botón de favorito funcional en el modal
                        IconButton(onClick = { onFavoriteToggle(product.id) }) {
                            Icon(
                                if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                                tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Imagen del producto en el modal
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
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
                                modifier = Modifier.size(120.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        // Badge de oferta en el modal
                        if (product.isOnSale) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(16.dp)
                                    .background(
                                        MaterialTheme.colorScheme.error,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "36% OFF",
                                    color = MaterialTheme.colorScheme.onError,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Información del producto
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Nombre del producto
                        Text(
                            text = product.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Marca
                        Text(
                            text = "Marca Premium",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Rating y reseñas
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row {
                                repeat(5) { index ->
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (index < product.rating.toInt()) Color(0xFFFFC107)
                                        else MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                            Text(
                                text = "${product.rating} - ${product.reviewCount} reseñas",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Precio
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$${product.price}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            product.originalPrice?.let { originalPrice ->
                                if (originalPrice > product.price) {
                                    Text(
                                        text = "$${originalPrice}",
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(start = 8.dp),
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                    Text(
                                        text = "36% OFF",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "18 disponibles",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Control de cantidad
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Cantidad:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { if (quantity > 1) quantity-- },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Disminuir")
                                }

                                Text(
                                    text = quantity.toString(),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                IconButton(
                                    onClick = { quantity++ },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Aumentar")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Tabs
                        TabRow(selectedTabIndex = selectedTab) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = {
                                        Text(
                                            text = title,
                                            fontSize = 12.sp
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Contenido de los tabs
                        when (selectedTab) {
                            0 -> { // Descripción
                                Text(
                                    text = "El ${product.name} redefine lo que es posible. Con su diseño innovador y tecnología de vanguardia, experimenta la excelencia en cada detalle.",
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            }
                            1 -> { // Características
                                Column {
                                    Text("• Diseño premium y elegante", fontSize = 14.sp)
                                    Text("• Tecnología de última generación", fontSize = 14.sp)
                                    Text("• Materiales de alta calidad", fontSize = 14.sp)
                                    Text("• Garantía del fabricante", fontSize = 14.sp)
                                    Text("• Envío gratis a todo el país", fontSize = 14.sp)
                                }
                            }
                            2 -> { // Especificaciones
                                Column {
                                    Text("Dimensiones: Según modelo", fontSize = 14.sp)
                                    Text("Peso: Variable", fontSize = 14.sp)
                                    Text("Material: Premium", fontSize = 14.sp)
                                    Text("Color: Varios disponibles", fontSize = 14.sp)
                                    Text("Garantía: 12 meses", fontSize = 14.sp)
                                }
                            }
                            3 -> { // Reseñas
                                Text(
                                    text = "Reseñas de clientes aparecerían aquí...",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(100.dp)) // Espacio para el botón fijo
                    }
                }

                // Botón fijo de agregar al carrito
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Button(
                        onClick = { /* Agregar al carrito */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Agregar al carrito",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}