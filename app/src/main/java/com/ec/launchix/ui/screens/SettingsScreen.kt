package com.ec.launchix.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var locationEnabled by remember { mutableStateOf(false) }
    var darkModeEnabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Sección de Cuenta
            SettingsSection(title = "Cuenta") {
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Información Personal",
                    subtitle = "Editar nombre, email y teléfono",
                    onClick = { }
                )
                SettingsItem(
                    icon = Icons.Default.LocationOn,
                    title = "Direcciones",
                    subtitle = "Gestionar direcciones de entrega",
                    onClick = { }
                )
                SettingsItem(
                    icon = Icons.Default.CreditCard,
                    title = "Métodos de Pago",
                    subtitle = "Tarjetas y opciones de pago",
                    onClick = { }
                )
            }

            // Sección de Preferencias
            SettingsSection(title = "Preferencias") {
                SettingsSwitchItem(
                    icon = Icons.Default.Notifications,
                    title = "Notificaciones Push",
                    subtitle = "Recibir notificaciones de pedidos y ofertas",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
                SettingsSwitchItem(
                    icon = Icons.Default.LocationOn,
                    title = "Ubicación",
                    subtitle = "Permitir acceso a la ubicación",
                    checked = locationEnabled,
                    onCheckedChange = { locationEnabled = it }
                )
                SettingsSwitchItem(
                    icon = Icons.Default.DarkMode,
                    title = "Modo Oscuro",
                    subtitle = "Cambiar apariencia de la app",
                    checked = darkModeEnabled,
                    onCheckedChange = { darkModeEnabled = it }
                )
            }

            // Sección de Pedidos
            SettingsSection(title = "Pedidos") {
                SettingsItem(
                    icon = Icons.Default.History,
                    title = "Historial de Pedidos",
                    subtitle = "Ver todos tus pedidos anteriores",
                    onClick = { navController.navigate("orders") }
                )
                SettingsItem(
                    icon = Icons.Default.Star,
                    title = "Productos Favoritos",
                    subtitle = "Gestionar lista de favoritos",
                    onClick = { navController.navigate("favorites") }
                )
            }

            // Sección de Soporte
            SettingsSection(title = "Soporte") {
                SettingsItem(
                    icon = Icons.Default.Help,
                    title = "Ayuda y FAQ",
                    subtitle = "Preguntas frecuentes y soporte",
                    onClick = { navController.navigate("help") }
                )
                SettingsItem(
                    icon = Icons.Default.ContactSupport,
                    title = "Contactar Soporte",
                    subtitle = "Chatea con nuestro equipo",
                    onClick = { }
                )
                SettingsItem(
                    icon = Icons.Default.Feedback,
                    title = "Enviar Comentarios",
                    subtitle = "Ayúdanos a mejorar la app",
                    onClick = { }
                )
            }

            // Sección de Información
            SettingsSection(title = "Información") {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Acerca de",
                    subtitle = "Versión 1.0.0",
                    onClick = { }
                )
                SettingsItem(
                    icon = Icons.Default.Description,
                    title = "Términos y Condiciones",
                    subtitle = "Leer términos de servicio",
                    onClick = { }
                )
                SettingsItem(
                    icon = Icons.Default.Security,
                    title = "Política de Privacidad",
                    subtitle = "Cómo manejamos tus datos",
                    onClick = { }
                )
            }

            // Botón de cerrar sesión
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = {
                    // Implementar lógica de cierre de sesión
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        content()
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsSwitchItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}