package com.ec.launchix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.ec.launchix.data.SampleData
import java.util.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    onServiceClick: (String) -> Unit = {},
    initialCategory: String? = null
) {
    var selectedCategory by remember(initialCategory) {
        mutableStateOf(initialCategory ?: "Todos")
    }
    var showServiceDetail by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf<com.ec.launchix.data.Service?>(null) }
    var favoriteServices by remember { mutableStateOf(setOf<String>()) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf("Relevancia") }
    var showBookingDialog by remember { mutableStateOf(false) }
    var bookingService by remember { mutableStateOf<com.ec.launchix.data.Service?>(null) }

    val context = LocalContext.current
    val listState = rememberLazyListState()

    val isScrolled by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }

    LaunchedEffect(Unit) {
        favoriteServices = SampleData.sampleServices
            .filter { it.isFavorite }
            .map { it.id }
            .toSet()
    }

    LaunchedEffect(initialCategory) {
        if (initialCategory != null) {
            selectedCategory = initialCategory
        }
    }

    val filteredServices = remember(selectedCategory, searchQuery, sortOption) {
        var services = if (selectedCategory == "Todos") {
            SampleData.sampleServices
        } else {
            SampleData.sampleServices.filter { it.category == selectedCategory }
        }

        if (searchQuery.isNotBlank()) {
            services = services.filter { service ->
                service.name.lowercase(Locale.getDefault()).contains(searchQuery.lowercase(Locale.getDefault())) ||
                        service.description.lowercase(Locale.getDefault()).contains(searchQuery.lowercase(Locale.getDefault())) ||
                        service.category.lowercase(Locale.getDefault()).contains(searchQuery.lowercase(Locale.getDefault()))
            }
        }

        // Aplicar ordenamiento
        services = when (sortOption) {
            "Precio: Menor a mayor" -> services.sortedBy { it.price }
            "Precio: Mayor a menor" -> services.sortedByDescending { it.price }
            "Mejor valorados" -> services.sortedByDescending { it.rating }
            "Más populares" -> services.sortedByDescending { it.reviewCount }
            else -> services // Relevancia (orden original)
        }

        services
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ServicesHeader(
            selectedCategory = selectedCategory,
            searchQuery = searchQuery,
            filteredServices = filteredServices,
            isSearchActive = isSearchActive,
            onQueryChange = { searchQuery = it },
            onSearchSubmit = { isSearchActive = false },
            onActiveChange = { isSearchActive = it },
            isCollapsed = isScrolled,
            onFilterClick = { showFilterDialog = true },
            onSortClick = { showSortDialog = true }
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isSearchActive && searchQuery.isBlank()) {
                item {
                    CategoryFilters(
                        selectedCategory = selectedCategory,
                        onCategoryChange = { selectedCategory = it }
                    )
                }
            }

            if (filteredServices.isEmpty()) {
                item {
                    EmptyServicesState(
                        searchQuery = searchQuery,
                        onClearSearch = {
                            searchQuery = ""
                            selectedCategory = "Todos"
                        }
                    )
                }
            } else {
                items(filteredServices.chunked(2)) { servicesRow ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        servicesRow.forEach { service ->
                            Box(modifier = Modifier.weight(1f)) {
                                ServiceGridCard(
                                    service = service,
                                    isFavorite = favoriteServices.contains(service.id),
                                    onFavoriteToggle = { id ->
                                        favoriteServices = if (favoriteServices.contains(id)) {
                                            favoriteServices - id
                                        } else favoriteServices + id

                                        val message = if (favoriteServices.contains(id)) {
                                            "Agregado a favoritos"
                                        } else {
                                            "Eliminado de favoritos"
                                        }
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    },
                                    onClick = {
                                        selectedService = service
                                        showServiceDetail = true
                                    },
                                    onContract = {
                                        bookingService = service
                                        showBookingDialog = true
                                    }
                                )
                            }
                        }
                        if (servicesRow.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    if (showServiceDetail && selectedService != null) {
        ServiceDetailModal(
            service = selectedService!!,
            isFavorite = favoriteServices.contains(selectedService!!.id),
            onFavoriteToggle = { serviceId ->
                favoriteServices = if (favoriteServices.contains(serviceId)) {
                    favoriteServices - serviceId
                } else {
                    favoriteServices + serviceId
                }

                val message = if (favoriteServices.contains(serviceId)) {
                    "Agregado a favoritos"
                } else {
                    "Eliminado de favoritos"
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showServiceDetail = false },
            onBooking = { service ->
                bookingService = service
                showBookingDialog = true
                showServiceDetail = false
            },
            onShare = {
                Toast.makeText(context, "Compartiendo servicio...", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showFilterDialog) {
        FilterDialog(
            onDismiss = { showFilterDialog = false },
            onApplyFilters = { minPrice, maxPrice, minRating ->
                // Aquí puedes aplicar los filtros adicionales
                showFilterDialog = false
                Toast.makeText(context, "Filtros aplicados", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showSortDialog) {
        SortDialog(
            currentSort = sortOption,
            onDismiss = { showSortDialog = false },
            onSortSelected = { option ->
                sortOption = option
                showSortDialog = false
                Toast.makeText(context, "Ordenado por: $option", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showBookingDialog && bookingService != null) {
        BookingDialog(
            service = bookingService!!,
            onDismiss = { showBookingDialog = false },
            onConfirm = { service, date, time ->
                showBookingDialog = false
                Toast.makeText(
                    context,
                    "Reserva confirmada para ${service.name} el $date a las $time",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServicesHeader(
    selectedCategory: String,
    searchQuery: String,
    filteredServices: List<com.ec.launchix.data.Service>,
    isSearchActive: Boolean,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onActiveChange: (Boolean) -> Unit,
    isCollapsed: Boolean,
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit
) {
    val topPadding by animateDpAsState(
        targetValue = if (isCollapsed) 8.dp else 20.dp,
        animationSpec = tween(durationMillis = 300)
    )

    val titleSize by animateDpAsState(
        targetValue = if (isCollapsed) 24.dp else 32.dp,
        animationSpec = tween(durationMillis = 300)
    )

    Surface(modifier = Modifier.fillMaxWidth(), color = Color.Transparent) {
        Box(
            modifier = Modifier.fillMaxWidth().background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFC107), // Amarillo dorado
                        Color(0xFFFFFFFF)
                    )
                )
            )
        ) {
            Box(
                modifier = Modifier.size(150.dp).offset(x = (-40).dp, y = (-40).dp)
                    .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(75.dp))
            )
            Box(
                modifier = Modifier.size(100.dp).offset(x = 300.dp, y = 20.dp)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(50.dp))
            )

            Column(
                modifier = Modifier.padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = topPadding,
                    bottom = 20.dp
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Servicios",
                            color = Color.White,
                            fontSize = titleSize.value.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )

                        if (!isCollapsed) {
                            if (selectedCategory != "Todos") {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(4.dp)
                                            .background(
                                                Color.White.copy(alpha = 0.8f),
                                                RoundedCornerShape(2.dp)
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        selectedCategory,
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            if (searchQuery.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .background(
                                            Color.White.copy(alpha = 0.2f),
                                            RoundedCornerShape(20.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Search,
                                            null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "${filteredServices.size} resultados",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (!isCollapsed) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.15f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { onFilterClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.FilterList,
                                    "Filtros",
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
                                    .clickable { onSortClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Sort,
                                    "Ordenar",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(if (isCollapsed) 12.dp else 24.dp))

                SearchBar(
                    query = searchQuery,
                    onQueryChange = onQueryChange,
                    onSearch = { onSearchSubmit() },
                    active = isSearchActive,
                    onActiveChange = onActiveChange,
                    placeholder = {
                        Text(
                            "Buscar servicios...",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            "Buscar",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange(""); onActiveChange(false) }) {
                                Icon(
                                    Icons.Default.Clear,
                                    "Limpiar búsqueda",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (searchQuery.isNotBlank()) {
                        val suggestions = SampleData.sampleServices
                            .filter {
                                it.name.lowercase(Locale.getDefault())
                                    .contains(searchQuery.lowercase(Locale.getDefault()))
                            }
                            .take(5)

                        LazyColumn {
                            items(suggestions) { service ->
                                ListItem(
                                    headlineContent = { Text(service.name, fontSize = 14.sp) },
                                    supportingContent = {
                                        Text(
                                            service.category,
                                            fontSize = 12.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    leadingContent = {
                                        Icon(
                                            Icons.Default.Build,
                                            null,
                                            modifier = Modifier.size(20.dp),
                                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        onQueryChange(service.name)
                                        onActiveChange(false)
                                    }
                                )
                            }
                        }
                    }
                }

                if (!isCollapsed) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
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
        items(listOf("Hogar", "Deportes", "Belleza", "Salud", "Negocios", "Educación", "Electrónicos", "Transporte")) { category ->
            FilterChip(
                onClick = { onCategoryChange(category) },
                label = { Text(category) },
                selected = selectedCategory == category,
                leadingIcon = if (selectedCategory == category) {
                    { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                } else null
            )
        }
    }
}

@Composable
private fun EmptyServicesState(
    searchQuery: String,
    onClearSearch: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        Color(0xFFFFC107).copy(alpha = 0.1f),
                        RoundedCornerShape(50.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (searchQuery.isNotBlank()) Icons.Default.SearchOff else Icons.Default.Build,
                    null,
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFFFFC107)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                if (searchQuery.isNotBlank()) "Sin resultados" else "Categoría vacía",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                if (searchQuery.isNotBlank()) {
                    "No encontramos servicios para \"$searchQuery\". Intenta con otros términos."
                } else "No hay servicios disponibles en esta categoría.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            if (searchQuery.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onClearSearch,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
                ) {
                    Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ver todos los servicios")
                }
            }
        }
    }
}

@Composable
fun ServiceGridCard(
    service: com.ec.launchix.data.Service,
    isFavorite: Boolean,
    onFavoriteToggle: (String) -> Unit,
    onClick: () -> Unit,
    onContract: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(310.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(service.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = service.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (service.isOnSale) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(MaterialTheme.colorScheme.error, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "OFERTA",
                            color = MaterialTheme.colorScheme.onError,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(
                    onClick = { onFavoriteToggle(service.id) },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                        .size(32.dp)
                ) {
                    Icon(
                        if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                        tint = if (isFavorite) Color.Red else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(
                    service.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    service.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.height(36.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFFFFC107)
                    )
                    Text(
                        "${service.rating} (${service.reviewCount})",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (service.duration.isNotEmpty()) {
                        Text(
                            " • ${service.duration}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "${service.price}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFC107)
                            )

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
                        if (service.isOnSale) {
                            Text(
                                "¡En oferta!",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(
                        onClick = { onContract() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            "Contratar",
                            tint = Color(0xFFFFC107)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailModal(
    service: com.ec.launchix.data.Service,
    isFavorite: Boolean,
    onFavoriteToggle: (String) -> Unit,
    onDismiss: () -> Unit,
    onBooking: (com.ec.launchix.data.Service) -> Unit,
    onShare: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var currentImageIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Descripción", "Incluye", "Detalles", "Reseñas")

    val serviceImages = remember {
        listOf(
            service.imageUrl,
            "https://images.unsplash.com/photo-1581578731548-c64695cc6952?w=800",
            "https://images.unsplash.com/photo-1600880292203-757bb62b4baf?w=800",
            "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=800"
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
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
                        IconButton(onClick = { onShare() }) {
                            Icon(Icons.Default.Share, "Compartir")
                        }
                        IconButton(onClick = { onFavoriteToggle(service.id) }) {
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
                    Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(serviceImages[currentImageIndex])
                                .crossfade(true)
                                .build(),
                            contentDescription = service.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        if (service.isOnSale) {
                            Box(
                                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                                    .background(MaterialTheme.colorScheme.error, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("OFERTA", color = MaterialTheme.colorScheme.onError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (serviceImages.size > 1) {
                            Row(
                                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                serviceImages.indices.forEach { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(if (index == currentImageIndex) 32.dp else 8.dp, 8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (index == currentImageIndex) Color.White
                                                else Color.White.copy(alpha = 0.5f)
                                            )
                                            .clickable { currentImageIndex = index }
                                    )
                                }
                            }

                            if (currentImageIndex > 0) {
                                IconButton(
                                    onClick = { currentImageIndex-- },
                                    modifier = Modifier.align(Alignment.CenterStart).padding(8.dp)
                                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(Icons.Default.ChevronLeft, "Anterior", tint = Color.White)
                                }
                            }

                            if (currentImageIndex < serviceImages.size - 1) {
                                IconButton(
                                    onClick = { currentImageIndex++ },
                                    modifier = Modifier.align(Alignment.CenterEnd).padding(8.dp)
                                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(Icons.Default.ChevronRight, "Siguiente", tint = Color.White)
                                }
                            }
                        }

                        Text(
                            text = "${currentImageIndex + 1}/${serviceImages.size}",
                            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(service.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(service.category, fontSize = 14.sp, color = Color(0xFFFFC107))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Row {
                                repeat(5) { index ->
                                    Icon(
                                        Icons.Default.Star,
                                        null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (index < service.rating.toInt()) Color(0xFFFFC107) else MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                            Text(
                                "${service.rating} - ${service.reviewCount} reseñas",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${service.price}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFC107))

                            service.originalPrice?.let { original ->
                                if (original > service.price) {
                                    Text(
                                        "${original}",
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(start = 8.dp),
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                    Text(
                                        "OFERTA",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Disponible", fontSize = 14.sp, color = MaterialTheme.colorScheme.tertiary)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                if (service.duration.isNotEmpty()) service.duration else "1-2 horas",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Icon(Icons.Default.Home, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("A domicilio", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Proveedor del servicio", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(0xFFFFC107), RoundedCornerShape(50.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("SP", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("ServiciosPro", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                        Text("5 años de experiencia", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text("Servicios completados:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("1250", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column {
                                        Text("Tiempo de respuesta:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("30 minutos", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

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

                        when (selectedTab) {
                            0 -> Text(
                                "El ${service.name} es un servicio profesional de alta calidad. Con años de experiencia, garantizamos resultados excepcionales y satisfacción total del cliente.",
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                            1 -> Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf(
                                    "Diagnóstico completo",
                                    "Herramientas profesionales",
                                    "Personal capacitado",
                                    "Materiales de calidad",
                                    "Garantía de 6 meses",
                                    "Servicio a domicilio"
                                ).forEach { Text("✓ $it", fontSize = 14.sp) }
                            }
                            2 -> Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf(
                                    "Tiempo estimado: ${if (service.duration.isNotEmpty()) service.duration else "1-2 horas"}",
                                    "Disponibilidad: Lunes a Sábado",
                                    "Horario: 8:00 AM - 6:00 PM",
                                    "Zona de cobertura: Toda la ciudad",
                                    "Precio fijo sin costos ocultos"
                                ).forEach { Text(it, fontSize = 14.sp) }
                            }
                            3 -> Text(
                                "Reseñas de clientes aparecerían aquí...",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Button(
                        onClick = { onBooking(service) },
                        modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
                    ) {
                        Icon(Icons.Default.DateRange, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reservar servicio", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onApplyFilters: (Double, Double, Float) -> Unit
) {
    var minPrice by remember { mutableStateOf(0.0) }
    var maxPrice by remember { mutableStateOf(1000.0) }
    var minRating by remember { mutableStateOf(0f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtros", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Rango de precio", fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = minPrice.toInt().toString(),
                        onValueChange = {
                            minPrice = it.toDoubleOrNull() ?: 0.0
                        },
                        label = { Text("Mínimo") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = maxPrice.toInt().toString(),
                        onValueChange = {
                            maxPrice = it.toDoubleOrNull() ?: 1000.0
                        },
                        label = { Text("Máximo") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Calificación mínima", fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        Icon(
                            Icons.Default.Star,
                            null,
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { minRating = (index + 1).toFloat() },
                            tint = if (index < minRating.toInt()) Color(0xFFFFC107) else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onApplyFilters(minPrice, maxPrice, minRating) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
            ) {
                Text("Aplicar filtros")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun SortDialog(
    currentSort: String,
    onDismiss: () -> Unit,
    onSortSelected: (String) -> Unit
) {
    val sortOptions = listOf(
        "Relevancia",
        "Precio: Menor a mayor",
        "Precio: Mayor a menor",
        "Mejor valorados",
        "Más populares"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ordenar por", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                sortOptions.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSortSelected(option) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentSort == option,
                            onClick = { onSortSelected(option) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(option, fontSize = 16.sp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDialog(
    service: com.ec.launchix.data.Service,
    onDismiss: () -> Unit,
    onConfirm: (com.ec.launchix.data.Service, String, String) -> Unit
) {
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val availableTimes = listOf(
        "8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM",
        "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM",
        "4:00 PM", "5:00 PM", "6:00 PM"
    )

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Reservar Servicio",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(service.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = service.name,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                service.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "${service.price}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFC107)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Selecciona una fecha",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = { selectedDate = it },
                    label = { Text("Fecha (DD/MM/AAAA)") },
                    placeholder = { Text("Ejemplo: 15/10/2025") },
                    leadingIcon = {
                        Icon(Icons.Default.CalendarToday, null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Selecciona una hora",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableTimes) { time ->
                        FilterChip(
                            onClick = { selectedTime = time },
                            label = { Text(time) },
                            selected = selectedTime == time,
                            leadingIcon = if (selectedTime == time) {
                                { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                            } else null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Notas adicionales (opcional)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Detalles adicionales") },
                    placeholder = { Text("Describe cualquier requerimiento especial...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFC107).copy(alpha = 0.1f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Resumen de la reserva",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Servicio:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(service.name, fontWeight = FontWeight.Medium)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Fecha:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                if (selectedDate.isNotEmpty()) selectedDate else "No seleccionada",
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Hora:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                if (selectedTime.isNotEmpty()) selectedTime else "No seleccionada",
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "${service.price}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFC107)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                                onConfirm(service, selectedDate, selectedTime)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Por favor selecciona fecha y hora",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                        enabled = selectedDate.isNotEmpty() && selectedTime.isNotEmpty()
                    ) {
                        Text("Confirmar reserva", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}