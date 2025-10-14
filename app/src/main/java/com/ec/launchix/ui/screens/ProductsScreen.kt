package com.ec.launchix.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ViewModel para manejar el carrito compartido
data class CartItemModel(
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String = "",
    val isService: Boolean = false
)

class CartViewModel : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItemModel>>(emptyList())
    val cartItems: StateFlow<List<CartItemModel>> = _cartItems.asStateFlow()

    fun addToCart(productId: String, name: String, price: Double, imageUrl: String, quantity: Int = 1) {
        _cartItems.update { currentItems ->
            val existingItem = currentItems.find { it.productId == productId }
            if (existingItem != null) {
                currentItems.map { item ->
                    if (item.productId == productId) {
                        item.copy(quantity = item.quantity + quantity)
                    } else item
                }
            } else {
                currentItems + CartItemModel(
                    productId = productId,
                    name = name,
                    price = price,
                    quantity = quantity,
                    imageUrl = imageUrl
                )
            }
        }
    }

    fun updateQuantity(productId: String, newQuantity: Int) {
        _cartItems.update { currentItems ->
            if (newQuantity <= 0) {
                currentItems.filter { it.productId != productId }
            } else {
                currentItems.map { item ->
                    if (item.productId == productId) {
                        item.copy(quantity = newQuantity)
                    } else item
                }
            }
        }
    }

    fun removeItem(productId: String) {
        _cartItems.update { currentItems ->
            currentItems.filter { it.productId != productId }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun getTotalItems(): Int {
        return _cartItems.value.sumOf { it.quantity }
    }

    fun getSubtotal(): Double {
        return _cartItems.value.sumOf { it.price * it.quantity }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    onProductClick: (String) -> Unit = {},
    initialCategory: String? = null,
    onNavigateToCart: () -> Unit = {},
    cartViewModel: CartViewModel = viewModel()
) {
    var selectedCategory by remember(initialCategory) { mutableStateOf(initialCategory ?: "Todos") }
    var showProductDetail by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<com.ec.launchix.data.Product?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var favoriteProducts by remember { mutableStateOf(setOf<String>()) }
    var showCartModal by remember { mutableStateOf(false) }

    val cartItems by cartViewModel.cartItems.collectAsState()

    LaunchedEffect(Unit) {
        favoriteProducts = SampleData.sampleProducts.filter { it.isFavorite }.map { it.id }.toSet()
    }

    LaunchedEffect(initialCategory) {
        initialCategory?.let { selectedCategory = it }
    }

    val filteredProducts = remember(selectedCategory, searchQuery) {
        var products = if (selectedCategory == "Todos") SampleData.sampleProducts
        else SampleData.sampleProducts.filter { it.category == selectedCategory }

        if (searchQuery.isNotBlank()) {
            val queryLower = searchQuery.lowercase(Locale.getDefault())
            products = products.filter {
                it.name.lowercase(Locale.getDefault()).contains(queryLower) ||
                        it.description.lowercase(Locale.getDefault()).contains(queryLower)
            }
        }
        products
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            ProductsHeader(
                selectedCategory = selectedCategory,
                searchQuery = searchQuery,
                filteredProducts = filteredProducts,
                isSearchActive = isSearchActive,
                onQueryChange = { searchQuery = it },
                onSearchSubmit = { isSearchActive = false },
                onActiveChange = { isSearchActive = it }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (searchQuery.isBlank()) {
                    item {
                        CategoryFilters(
                            selectedCategory = selectedCategory,
                            onCategoryChange = { selectedCategory = it }
                        )
                    }
                }

                if (filteredProducts.isEmpty()) {
                    item {
                        EmptyProductsState(
                            searchQuery = searchQuery,
                            onClearSearch = {
                                searchQuery = ""
                                selectedCategory = "Todos"
                            }
                        )
                    }
                } else {
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
                                        onFavoriteToggle = { id ->
                                            favoriteProducts = if (favoriteProducts.contains(id)) {
                                                favoriteProducts - id
                                            } else favoriteProducts + id
                                        },
                                        onClick = {
                                            selectedProduct = product
                                            showProductDetail = true
                                        },
                                        onAddToCart = {
                                            cartViewModel.addToCart(
                                                productId = product.id,
                                                name = product.name,
                                                price = product.price,
                                                imageUrl = product.imageUrl,
                                                quantity = 1
                                            )
                                            showCartModal = true
                                        }
                                    )
                                }
                            }
                            if (productsRow.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        if (cartItems.isNotEmpty()) {
            FloatingActionButton(
                onClick = { showCartModal = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ) {
                            Text(cartViewModel.getTotalItems().toString())
                        }
                    }
                ) {
                    Icon(Icons.Default.ShoppingCart, "Ver carrito")
                }
            }
        }
    }

    if (showProductDetail && selectedProduct != null) {
        ProductDetailModal(
            product = selectedProduct!!,
            isFavorite = favoriteProducts.contains(selectedProduct!!.id),
            onFavoriteToggle = { id ->
                favoriteProducts = if (favoriteProducts.contains(id)) {
                    favoriteProducts - id
                } else favoriteProducts + id
            },
            onDismiss = { showProductDetail = false },
            onAddToCart = { quantity ->
                cartViewModel.addToCart(
                    productId = selectedProduct!!.id,
                    name = selectedProduct!!.name,
                    price = selectedProduct!!.price,
                    imageUrl = selectedProduct!!.imageUrl,
                    quantity = quantity
                )
                showProductDetail = false
                showCartModal = true
            }
        )
    }

    if (showCartModal) {
        CartModalDialog(
            cartItems = cartItems.map {
                CartItemData(
                    productId = it.productId,
                    name = it.name,
                    price = it.price,
                    quantity = it.quantity,
                    imageUrl = it.imageUrl
                )
            },
            onDismiss = { showCartModal = false },
            onUpdateQuantity = { productId, newQuantity ->
                cartViewModel.updateQuantity(productId, newQuantity)
            },
            onRemoveItem = { productId ->
                cartViewModel.removeItem(productId)
            },
            onFinishPurchase = {
                showCartModal = false
                onNavigateToCart()
            }
        )
    }
}

data class CartItemData(
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String = ""
)

@Composable
fun CartModalDialog(
    cartItems: List<CartItemData>,
    onDismiss: () -> Unit,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onFinishPurchase: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.8f),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mi Carrito",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                if (cartItems.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tu carrito está vacío",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(cartItems) { item ->
                            CartModalItemCard(
                                item = item,
                                onQuantityChange = { newQuantity ->
                                    onUpdateQuantity(item.productId, newQuantity)
                                },
                                onRemove = { onRemoveItem(item.productId) }
                            )
                        }
                    }

                    Divider()

                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Subtotal:", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                text = "$${String.format("%.2f", cartItems.sumOf { it.price * it.quantity })}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Envío:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "Gratis",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Total:", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                text = "$${String.format("%.2f", cartItems.sumOf { it.price * it.quantity })}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onFinishPurchase,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Finalizar Compra", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartModalItemCard(
    item: CartItemData,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.size(70.dp).clip(RoundedCornerShape(8.dp))) {
                if (item.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize().background(
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
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = item.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Eliminar",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${String.format("%.2f", item.price)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(onClick = { onQuantityChange(item.quantity - 1) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Remove, contentDescription = "Disminuir", modifier = Modifier.size(16.dp))
                        }

                        Text(
                            text = "${item.quantity}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        IconButton(onClick = { onQuantityChange(item.quantity + 1) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Add, contentDescription = "Aumentar", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductsHeader(
    selectedCategory: String,
    searchQuery: String,
    filteredProducts: List<com.ec.launchix.data.Product>,
    isSearchActive: Boolean,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onActiveChange: (Boolean) -> Unit
) {
    Surface(modifier = Modifier.fillMaxWidth(), color = Color.Transparent) {
        Box(
            modifier = Modifier.fillMaxWidth().background(
                brush = Brush.verticalGradient(listOf(Color(0xFFFFB800), Color(0xFFFFFFFF)))
            )
        ) {
            DecorativeElements()

            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HeaderTitle(selectedCategory = selectedCategory, searchQuery = searchQuery, resultsCount = filteredProducts.size)

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        HeaderIconButton(icon = Icons.Default.FilterList, contentDescription = "Filtros")
                        HeaderIconButton(icon = Icons.Default.Sort, contentDescription = "Ordenar")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                SearchBar(
                    query = searchQuery,
                    onQueryChange = onQueryChange,
                    onSearch = { onSearchSubmit() },
                    active = isSearchActive,
                    onActiveChange = onActiveChange,
                    placeholder = { Text("Buscar productos...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
                    leadingIcon = { Icon(Icons.Default.Search, "Buscar", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange(""); onActiveChange(false) }) {
                                Icon(Icons.Default.Clear, "Limpiar búsqueda", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    SearchSuggestions(searchQuery = searchQuery, onSuggestionClick = { suggestion ->
                        onQueryChange(suggestion)
                        onActiveChange(false)
                    })
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DecorativeElements() {
    Box(
        modifier = Modifier.size(150.dp).offset(x = (-40).dp, y = (-40).dp)
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(75.dp))
    )
    Box(
        modifier = Modifier.size(100.dp).offset(x = 300.dp, y = 20.dp)
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(50.dp))
    )
}

@Composable
private fun HeaderTitle(selectedCategory: String, searchQuery: String, resultsCount: Int) {
    Column {
        Text("Productos", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black, letterSpacing = (-0.5).sp)

        if (selectedCategory != "Todos") {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(4.dp).background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(2.dp)))
                Spacer(modifier = Modifier.width(8.dp))
                Text(selectedCategory, color = Color.White.copy(alpha = 0.9f), fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }
        }

        if (searchQuery.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier.background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("$resultsCount resultados", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun HeaderIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String) {
    Box(
        modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp)).clickable { },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription, tint = Color.White, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun SearchSuggestions(searchQuery: String, onSuggestionClick: (String) -> Unit) {
    if (searchQuery.isNotBlank()) {
        val suggestions = SampleData.sampleProducts
            .filter { it.name.lowercase(Locale.getDefault()).contains(searchQuery.lowercase(Locale.getDefault())) }
            .take(5)

        LazyColumn {
            items(suggestions) { product ->
                ListItem(
                    headlineContent = { Text(product.name, fontSize = 14.sp) },
                    supportingContent = { Text(product.description, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    leadingContent = {
                        Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    },
                    modifier = Modifier.clickable { onSuggestionClick(product.name) }
                )
            }
        }
    }
}

@Composable
private fun CategoryFilters(selectedCategory: String, onCategoryChange: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            FilterChip(
                onClick = { onCategoryChange("Todos") },
                label = { Text("Todos") },
                selected = selectedCategory == "Todos",
                leadingIcon = if (selectedCategory == "Todos") {
                    { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                } else null
            )
        }
        items(SampleData.sampleCategories) { category ->
            FilterChip(
                onClick = { onCategoryChange(category.name) },
                label = { Text(category.name) },
                selected = selectedCategory == category.name,
                leadingIcon = if (selectedCategory == category.name) {
                    { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                } else null
            )
        }
    }
}

@Composable
private fun EmptyProductsState(searchQuery: String, onClearSearch: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(100.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(50.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (searchQuery.isNotBlank()) Icons.Default.SearchOff else Icons.Default.Inventory2,
                    null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(if (searchQuery.isNotBlank()) "Sin resultados" else "Categoría vacía", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                if (searchQuery.isNotBlank()) {
                    "No encontramos productos para \"$searchQuery\". Intenta con otros términos."
                } else "No hay productos disponibles en esta categoría.",
                fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center
            )

            if (searchQuery.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onClearSearch, shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ver todos los productos")
                }
            }
        }
    }
}

@Composable
fun ProductGridCard(
    product: com.ec.launchix.data.Product,
    isFavorite: Boolean,
    onFavoriteToggle: (String) -> Unit,
    onClick: () -> Unit,
    onAddToCart: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(product.imageUrl).crossfade(true).build(),
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

                IconButton(
                    onClick = { onFavoriteToggle(product.id) },
                    modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape).size(32.dp)
                ) {
                    Icon(
                        if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                        tint = if (isFavorite) Color.Red else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(product.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    product.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFC107))
                    Text(
                        "${product.rating} (${product.reviewCount})",
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "${product.price}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        product.originalPrice?.let { original ->
                            if (original > product.price) {
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

                    IconButton(onClick = { onAddToCart() }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.AddShoppingCart, "Agregar al carrito", tint = MaterialTheme.colorScheme.primary)
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
    onDismiss: () -> Unit,
    onAddToCart: (Int) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    var quantity by remember { mutableStateOf(1) }
    val tabs = listOf("Descripción", "Características", "Especificaciones", "Reseñas")

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.ArrowBack, "Cerrar")
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Share, "Compartir")
                        }
                        IconButton(onClick = { onFavoriteToggle(product.id) }) {
                            Icon(
                                if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                                tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )

                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                    ProductImage(product = product)

                    Column(modifier = Modifier.padding(16.dp)) {
                        ProductInfo(product = product)
                        Spacer(modifier = Modifier.height(16.dp))
                        QuantitySelector(quantity = quantity, onQuantityChange = { quantity = it })
                        Spacer(modifier = Modifier.height(24.dp))

                        TabRow(selectedTabIndex = selectedTab) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = { Text(title, fontSize = 12.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        TabContent(selectedTab = selectedTab, product = product)
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }

                AddToCartButton(onAddToCart = { onAddToCart(quantity) })
            }
        }
    }
}

@Composable
private fun ProductImage(product: com.ec.launchix.data.Product) {
    Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(product.imageUrl).crossfade(true).build(),
            contentDescription = product.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        if (product.isOnSale) {
            Box(
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                    .background(MaterialTheme.colorScheme.error, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("36% OFF", color = MaterialTheme.colorScheme.onError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ProductInfo(product: com.ec.launchix.data.Product) {
    Text(product.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(4.dp))
    Text("Marca Premium", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
    Spacer(modifier = Modifier.height(8.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        Row {
            repeat(5) { index ->
                Icon(
                    Icons.Default.Star,
                    null,
                    modifier = Modifier.size(16.dp),
                    tint = if (index < product.rating.toInt()) Color(0xFFFFC107) else MaterialTheme.colorScheme.outline
                )
            }
        }
        Text(
            "${product.rating} - ${product.reviewCount} reseñas",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp)
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("${product.price}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

        product.originalPrice?.let { original ->
            if (original > product.price) {
                Text(
                    "${original}",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp),
                    textDecoration = TextDecoration.LineThrough
                )
                Text(
                    "36% OFF",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
    Text("18 disponibles", fontSize = 14.sp, color = MaterialTheme.colorScheme.tertiary)
}

@Composable
private fun QuantitySelector(quantity: Int, onQuantityChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Cantidad:", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.width(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (quantity > 1) onQuantityChange(quantity - 1) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Remove, "Disminuir")
            }
            Text(quantity.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
            IconButton(onClick = { onQuantityChange(quantity + 1) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Add, "Aumentar")
            }
        }
    }
}

@Composable
private fun TabContent(selectedTab: Int, product: com.ec.launchix.data.Product) {
    when (selectedTab) {
        0 -> Text(
            "El ${product.name} redefine lo que es posible. Con su diseño innovador y tecnología de vanguardia, experimenta la excelencia en cada detalle.",
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
        1 -> Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf(
                "Diseño premium y elegante",
                "Tecnología de última generación",
                "Materiales de alta calidad",
                "Garantía del fabricante",
                "Envío gratis a todo el país"
            ).forEach { Text("• $it", fontSize = 14.sp) }
        }
        2 -> Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf(
                "Dimensiones: Según modelo",
                "Peso: Variable",
                "Material: Premium",
                "Color: Varios disponibles",
                "Garantía: 12 meses"
            ).forEach { Text(it, fontSize = 14.sp) }
        }
        3 -> Text(
            "Reseñas de clientes aparecerían aquí...",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AddToCartButton(onAddToCart: () -> Unit = {}) {
    Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
        Button(
            onClick = { onAddToCart() },
            modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Agregar al carrito", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}