package com.ec.launchix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.ec.launchix.data.SampleData
import java.util.*

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

    val filteredServices = remember(selectedCategory, searchQuery) {
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
        services
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                        Text(
                            text = "Servicios",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (selectedCategory != "Todos" || searchQuery.isNotBlank()) {
                            val filterText = buildString {
                                if (selectedCategory != "Todos") append("Categoría: $selectedCategory")
                                if (searchQuery.isNotBlank()) {
                                    if (selectedCategory != "Todos") append(" • ")
                                    append("Búsqueda: '$searchQuery'")
                                }
                            }
                            Text(
                                text = filterText,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                        }
                    }
                    Row {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.FilterList, "Filtros", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Sort, "Ordenar", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { searchQuery = it; isSearchActive = false },
                    active = isSearchActive,
                    onActiveChange = { isSearchActive = it },
                    placeholder = { Text("Buscar servicios...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    leadingIcon = { Icon(Icons.Default.Search, "Buscar", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = ""; isSearchActive = false }) {
                                Icon(Icons.Default.Clear, "Limpiar búsqueda", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (searchQuery.isNotBlank()) {
                        val suggestions = SampleData.sampleServices
                            .filter { it.name.lowercase(Locale.getDefault()).contains(searchQuery.lowercase(Locale.getDefault())) }
                            .take(5)
                        LazyColumn {
                            items(suggestions) { service ->
                                ListItem(
                                    headlineContent = { Text(service.name) },
                                    supportingContent = { Text(service.category) },
                                    leadingContent = { Icon(Icons.Default.Build, null, modifier = Modifier.size(24.dp)) },
                                    modifier = Modifier.clickable { searchQuery = service.name; isSearchActive = false }
                                )
                            }
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isSearchActive) {
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                onClick = { selectedCategory = "Todos" },
                                label = { Text("Todos") },
                                selected = selectedCategory == "Todos"
                            )
                        }
                        items(listOf("Hogar", "Deportes", "Belleza", "Salud", "Negocios", "Educación", "Electrónicos", "Transporte")) { category ->
                            FilterChip(
                                onClick = { selectedCategory = category },
                                label = { Text(category) },
                                selected = selectedCategory == category
                            )
                        }
                    }
                }
            }

            if (filteredServices.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            if (searchQuery.isNotBlank()) Icons.Default.SearchOff else Icons.Default.Category,
                            null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        val message = when {
                            searchQuery.isNotBlank() && selectedCategory != "Todos" ->
                                "No se encontraron servicios para '$searchQuery' en la categoría $selectedCategory"
                            searchQuery.isNotBlank() -> "No se encontraron servicios para '$searchQuery'"
                            selectedCategory != "Todos" -> "No hay servicios en esta categoría"
                            else -> "No hay servicios disponibles"
                        }
                        Text(
                            text = message,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        if (searchQuery.isNotBlank() || selectedCategory != "Todos") {
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(onClick = { searchQuery = ""; selectedCategory = "Todos" }) {
                                Icon(Icons.Default.Clear, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Limpiar filtros")
                            }
                        }
                    }
                }
            } else {
                item {
                    Text(
                        text = "${filteredServices.size} servicio${if (filteredServices.size != 1) "s" else ""} encontrado${if (filteredServices.size != 1) "s" else ""}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
                items(filteredServices) { service ->
                    ServiceListCard(
                        service = service,
                        isFavorite = favoriteServices.contains(service.id),
                        onFavoriteToggle = { serviceId ->
                            favoriteServices = if (favoriteServices.contains(serviceId)) {
                                favoriteServices - serviceId
                            } else {
                                favoriteServices + serviceId
                            }
                        },
                        onClick = { selectedService = service; showServiceDetail = true }
                    )
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
            },
            onDismiss = { showServiceDetail = false }
        )
    }
}

@Composable
fun ServiceListCard(
    service: com.ec.launchix.data.Service,
    isFavorite: Boolean,
    onFavoriteToggle: (String) -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                AsyncImage(
                    model = service.imageUrl,
                    contentDescription = service.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = service.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { onFavoriteToggle(service.id) }, modifier = Modifier.size(24.dp)) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            "Favorito",
                            tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = service.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, modifier = Modifier.size(16.dp), tint = Color(0xFFFFC107))
                    Text("${service.rating}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp))
                    Text(" (${service.reviewCount} reseñas)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (service.duration.isNotEmpty()) {
                        Text(" • ${service.duration}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("$${service.price}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            service.originalPrice?.let { originalPrice ->
                                if (originalPrice > service.price) {
                                    Text("$${originalPrice}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 8.dp))
                                }
                            }
                        }
                        if (service.isOnSale) {
                            Text("¡En oferta!", fontSize = 12.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Medium)
                        }
                    }
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Contratar", color = MaterialTheme.colorScheme.onPrimary, fontSize = 14.sp)
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
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var currentImageIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Descripción", "Incluye", "Detalles", "Reseñas")

    // Lista de imágenes de ejemplo (puedes cambiarlas por las reales del servicio)
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
                        IconButton(onClick = { }) {
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
                        Card(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(0.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            AsyncImage(
                                model = serviceImages[currentImageIndex],
                                contentDescription = service.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Botón anterior
                        if (currentImageIndex > 0) {
                            IconButton(
                                onClick = { currentImageIndex-- },
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(8.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                        RoundedCornerShape(50.dp)
                                    )
                            ) {
                                Icon(Icons.Default.ChevronLeft, "Anterior", tint = MaterialTheme.colorScheme.onSurface)
                            }
                        }

                        // Botón siguiente
                        if (currentImageIndex < serviceImages.size - 1) {
                            IconButton(
                                onClick = { currentImageIndex++ },
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(8.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                        RoundedCornerShape(50.dp)
                                    )
                            ) {
                                Icon(Icons.Default.ChevronRight, "Siguiente", tint = MaterialTheme.colorScheme.onSurface)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Disponible", color = MaterialTheme.colorScheme.onTertiary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // Indicadores de imagen
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            serviceImages.forEachIndexed { index, _ ->
                                Box(
                                    modifier = Modifier
                                        .size(if (index == currentImageIndex) 10.dp else 8.dp)
                                        .background(
                                            if (index == currentImageIndex) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                                            RoundedCornerShape(50.dp)
                                        )
                                        .clickable { currentImageIndex = index }
                                )
                            }
                        }

                        // Contador de imágenes
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                                .background(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                "${currentImageIndex + 1}/${serviceImages.size}",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Miniaturas de imágenes
                    LazyRow(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(serviceImages.size) { index ->
                            Card(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clickable { currentImageIndex = index },
                                shape = RoundedCornerShape(8.dp),
                                border = if (index == currentImageIndex) {
                                    androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                } else null,
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                AsyncImage(
                                    model = serviceImages[index],
                                    contentDescription = "Imagen ${index + 1}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(service.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(service.category, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
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
                            Text("$${service.price}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("Precio fijo", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 8.dp))
                        }
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
                                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("TP", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("TechFix Pro", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
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
                                Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title, fontSize = 12.sp) })
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        when (selectedTab) {
                            0 -> Text(service.description, fontSize = 14.sp, lineHeight = 20.sp)
                            1 -> Column {
                                Text("✓ Diagnóstico completo del dispositivo", fontSize = 14.sp)
                                Text("✓ Reparación con repuestos originales", fontSize = 14.sp)
                                Text("✓ Limpieza interna del dispositivo", fontSize = 14.sp)
                                Text("✓ Pruebas de funcionamiento", fontSize = 14.sp)
                                Text("✓ Garantía de 6 meses", fontSize = 14.sp)
                                Text("✓ Servicio a domicilio", fontSize = 14.sp)
                            }
                            2 -> Column {
                                Text("Marcas compatibles: iPhone, Samsung, Huawei, Xiaomi", fontSize = 14.sp)
                                Text("Tiempo estimado: ${if (service.duration.isNotEmpty()) service.duration else "1-2 horas"}", fontSize = 14.sp)
                                Text("Disponibilidad: Lunes a Sábado", fontSize = 14.sp)
                                Text("Horario: 8:00 AM - 6:00 PM", fontSize = 14.sp)
                                Text("Zona de cobertura: Toda la ciudad", fontSize = 14.sp)
                            }
                            3 -> Text("Reseñas de clientes aparecerían aquí...", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }

                Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
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