package com.ec.launchix.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    userName: String,
    userEmail: String,
    userPhone: String,
    onUserInfoChange: (name: String, email: String, phone: String) -> Unit,
    isDarkMode: Boolean = false,
    onDarkModeToggle: (Boolean) -> Unit = {}
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var locationEnabled by remember { mutableStateOf(false) }

    var showPersonalInfoDialog by remember { mutableStateOf(false) }
    var showAddressDialog by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsSection(title = "Cuenta") {
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Información Personal",
                    subtitle = userName,
                    onClick = { showPersonalInfoDialog = true }
                )
                SettingsItem(
                    icon = Icons.Default.LocationOn,
                    title = "Direcciones",
                    subtitle = "Gestionar direcciones de entrega",
                    onClick = { showAddressDialog = true }
                )
                SettingsItem(
                    icon = Icons.Default.CreditCard,
                    title = "Métodos de Pago",
                    subtitle = "Tarjetas y opciones de pago",
                    onClick = { showPaymentDialog = true }
                )
            }

            SettingsSection(title = "Preferencias") {
                SettingsSwitchItem(
                    icon = Icons.Default.Notifications,
                    title = "Notificaciones Push",
                    subtitle = if (notificationsEnabled) "Activadas" else "Desactivadas",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
                SettingsSwitchItem(
                    icon = Icons.Default.LocationOn,
                    title = "Ubicación",
                    subtitle = if (locationEnabled) "Acceso permitido" else "Acceso denegado",
                    checked = locationEnabled,
                    onCheckedChange = { locationEnabled = it }
                )
                SettingsSwitchItem(
                    icon = Icons.Default.DarkMode,
                    title = "Modo Oscuro",
                    subtitle = if (isDarkMode) "Tema oscuro" else "Tema claro",
                    checked = isDarkMode,
                    onCheckedChange = onDarkModeToggle
                )
            }

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
                    onClick = { showContactDialog = true }
                )
                SettingsItem(
                    icon = Icons.Default.Feedback,
                    title = "Enviar Comentarios",
                    subtitle = "Ayúdanos a mejorar la app",
                    onClick = { showFeedbackDialog = true }
                )
            }

            SettingsSection(title = "Información") {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Acerca de",
                    subtitle = "Versión 1.0.0",
                    onClick = { showAboutDialog = true }
                )
                SettingsItem(
                    icon = Icons.Default.Description,
                    title = "Términos y Condiciones",
                    subtitle = "Leer términos de servicio",
                    onClick = { showTermsDialog = true }
                )
                SettingsItem(
                    icon = Icons.Default.Security,
                    title = "Política de Privacidad",
                    subtitle = "Cómo manejamos tus datos",
                    onClick = { showPrivacyDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = { showLogoutDialog = true },
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

    if (showPersonalInfoDialog) {
        var tempName by rememberSaveable { mutableStateOf(userName) }
        var tempEmail by rememberSaveable { mutableStateOf(userEmail) }
        var tempPhone by rememberSaveable { mutableStateOf(userPhone) }

        LaunchedEffect(userName, userEmail, userPhone) {
            tempName = userName
            tempEmail = userEmail
            tempPhone = userPhone
        }

        AlertDialog(
            onDismissRequest = { showPersonalInfoDialog = false },
            title = { Text("Información Personal") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Nombre completo") },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempEmail,
                        onValueChange = { tempEmail = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempPhone,
                        onValueChange = { tempPhone = it },
                        label = { Text("Teléfono") },
                        leadingIcon = { Icon(Icons.Default.Phone, null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onUserInfoChange(tempName, tempEmail, tempPhone)
                    showPersonalInfoDialog = false
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPersonalInfoDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = { Icon(Icons.Default.ExitToApp, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        navController.navigate("profile") {
                            popUpTo("profile") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cerrar Sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
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