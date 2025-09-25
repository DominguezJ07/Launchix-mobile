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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ec.launchix.data.SampleData
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    onServiceClick: (String) -> Unit = {},
    initialCategory: String? = null
) {
    // Estados existentes
    var selectedCategory by remember(initialCategory) {
        mutableStateOf(initialCategory ?: "Todos")
    }
    var showServiceDetail by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf<com.ec.launchix.data.Service?>(null) }
    var favoriteServices by remember { mutableStateOf(setOf<String>()) }

    // NUEVO: Estados para la búsqueda
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    // Inicializar favoritos
    LaunchedEffect(Unit) {
        favoriteServices = SampleData.sampleServices
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

    // MODIFICADO: Filtrar servicios según la categoría y búsqueda
    val filteredServices = remember(selectedCategory, searchQuery) {
        var services = if (selectedCategory == "Todos") {
            SampleData.sampleServices
        } else {
            SampleData.sampleServices.filter { service ->
                service.category == selectedCategory
            }
        }

        // Aplicar filtro de búsqueda si hay texto
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
        // Header
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
                            text = "Servicios",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        // MODIFICADO: Mostrar información de filtros aplicados
                        if (selectedCategory != "Todos" || searchQuery.isNotBlank()) {
                            val filterText = buildString {
                                if (selectedCategory != "Todos") {
                                    append("Categoría: $selectedCategory")
                                }
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
                        IconButton(onClick = { /* Filtros */ }) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Filtros",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        IconButton(onClick = { /* Ordenar */ }) {
                            Icon(
                                Icons.Default.Sort,
                                contentDescription = "Ordenar",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // MODIFICADO: Barra de búsqueda funcional
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { newQuery ->
                        searchQuery = newQuery
                    },
                    onSearch = { query ->
                        searchQuery = query
                        isSearchActive = false
                    },
                    active = isSearchActive,
                    onActiveChange = { active ->
                        isSearchActive = active
                    },
                    placeholder = {
                        Text(
                            "Buscar servicios...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        // NUEVO: Botón para limpiar búsqueda
                        if (searchQuery.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    searchQuery = ""
                                    isSearchActive = false
                                }
                            ) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Limpiar búsqueda",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // NUEVO: Sugerencias de búsqueda cuando está activa
                    if (searchQuery.isNotBlank()) {
                        val suggestions = SampleData.sampleServices
                            .filter { service ->
                                service.name.lowercase(Locale.getDefault()).contains(searchQuery.lowercase(Locale.getDefault()))
                            }
                            .take(5) // Limitar a 5 sugerencias

                        LazyColumn {
                            items(suggestions) { service ->
                                ListItem(
                                    headlineContent = { Text(service.name) },
                                    supportingContent = { Text(service.category) },
                                    leadingContent = {
                                        Icon(
                                            Icons.Default.Build,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        searchQuery = service.name
                                        isSearchActive = false
                                    }
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
            // Filtros por categoría (solo si no hay búsqueda activa)
            if (!isSearchActive) {
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                onClick = {
                                    selectedCategory = "Todos"
                                    // Opcional: limpiar búsqueda al cambiar categoría
                                    // searchQuery = ""
                                },
                                label = { Text("Todos") },
                                selected = selectedCategory == "Todos"
                            )
                        }
                        items(listOf("Hogar", "Deportes", "Belleza", "Salud", "Negocios", "Educación", "Electrónicos", "Transporte")) { category ->
                            FilterChip(
                                onClick = {
                                    selectedCategory = category
                                    // Opcional: limpiar búsqueda al cambiar categoría
                                    // searchQuery = ""
                                },
                                label = { Text(category) },
                                selected = selectedCategory == category
                            )
                        }
                    }
                }
            }

            // MODIFICADO: Mostrar diferentes mensajes según el tipo de filtro
            if (filteredServices.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            if (searchQuery.isNotBlank()) Icons.Default.SearchOff else Icons.Default.Category,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        val message = when {
                            searchQuery.isNotBlank() && selectedCategory != "Todos" ->
                                "No se encontraron servicios para '$searchQuery' en la categoría $selectedCategory"
                            searchQuery.isNotBlank() ->
                                "No se encontraron servicios para '$searchQuery'"
                            selectedCategory != "Todos" ->
                                "No hay servicios en esta categoría"
                            else ->
                                "No hay servicios disponibles"
                        }

                        Text(
                            text = message,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        // NUEVO: Botón para limpiar filtros
                        if (searchQuery.isNotBlank() || selectedCategory != "Todos") {
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(
                                onClick = {
                                    searchQuery = ""
                                    selectedCategory = "Todos"
                                }
                            ) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Limpiar filtros")
                            }
                        }
                    }
                }
            } else {
                // NUEVO: Mostrar contador de resultados
                item {
                    Text(
                        text = "${filteredServices.size} servicio${if (filteredServices.size != 1) "s" else ""} encontrado${if (filteredServices.size != 1) "s" else ""}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                // Lista de servicios filtrados
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
                        onClick = {
                            selectedService = service
                            showServiceDetail = true
                        }
                    )
                }
            }
        }
    }

    // Modal de detalles del servicio
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
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen del servicio
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.secondaryContainer,
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                            )
                        ),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Build,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
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

                    IconButton(
                        onClick = { onFavoriteToggle(service.id) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
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

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFFFFC107)
                    )
                    Text(
                        text = "${service.rating}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Text(
                        text = " (${service.reviewCount} reseñas)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (service.duration.isNotEmpty()) {
                        Text(
                            text = " • ${service.duration}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
                                text = "$${service.price}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            service.originalPrice?.let { originalPrice ->
                                if (originalPrice > service.price) {
                                    Text(
                                        text = "$${originalPrice}",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }

                        if (service.isOnSale) {
                            Text(
                                text = "¡En oferta!",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Button(
                        onClick = { /* Contratar servicio */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Contratar",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 14.sp
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
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf("Descripción", "Incluye", "Detalles", "Reseñas")

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
                        IconButton(onClick = { onFavoriteToggle(service.id) }) {
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
                    // Imagen del servicio con carousel de imágenes
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
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
                            modifier = Modifier.size(120.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        // Badge de disponibilidad
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .background(
                                    MaterialTheme.colorScheme.tertiary,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Disponible",
                                color = MaterialTheme.colorScheme.onTertiary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Indicador de imágenes
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(4) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(if (index == 0) 10.dp else 8.dp)
                                        .background(
                                            if (index == 0) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                                            RoundedCornerShape(50.dp)
                                        )
                                )
                            }
                        }
                    }

                    // Información del servicio
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Nombre del servicio
                        Text(
                            text = service.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Mostrar la categoría del servicio
                        Text(
                            text = service.category,
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
                                        tint = if (index < service.rating.toInt()) Color(0xFFFFC107)
                                        else MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                            Text(
                                text = "${service.rating} - ${service.reviewCount} reseñas",
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
                                text = "$${service.price}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Precio fijo",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Información de tiempo
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (service.duration.isNotEmpty()) service.duration else "1-2 horas",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Icon(
                                Icons.Default.Home,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "A domicilio",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Información del proveedor
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Proveedor del servicio",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Avatar del proveedor
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                MaterialTheme.colorScheme.primary,
                                                RoundedCornerShape(50.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "TP",
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "TechFix Pro",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "5 años de experiencia",
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

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
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
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
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
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
                                    text = service.description,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            }
                            1 -> { // Incluye
                                Column {
                                    Text("✓ Diagnóstico completo del dispositivo", fontSize = 14.sp)
                                    Text("✓ Reparación con repuestos originales", fontSize = 14.sp)
                                    Text("✓ Limpieza interna del dispositivo", fontSize = 14.sp)
                                    Text("✓ Pruebas de funcionamiento", fontSize = 14.sp)
                                    Text("✓ Garantía de 6 meses", fontSize = 14.sp)
                                    Text("✓ Servicio a domicilio", fontSize = 14.sp)
                                }
                            }
                            2 -> { // Detalles
                                Column {
                                    Text("Marcas compatibles: iPhone, Samsung, Huawei, Xiaomi", fontSize = 14.sp)
                                    Text("Tiempo estimado: ${if (service.duration.isNotEmpty()) service.duration else "1-2 horas"}", fontSize = 14.sp)
                                    Text("Disponibilidad: Lunes a Sábado", fontSize = 14.sp)
                                    Text("Horario: 8:00 AM - 6:00 PM", fontSize = 14.sp)
                                    Text("Zona de cobertura: Toda la ciudad", fontSize = 14.sp)
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

                // Botón fijo de reservar servicio
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Button(
                        onClick = { /* Reservar servicio */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Reservar servicio",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}