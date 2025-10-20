package com.ec.launchix.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    isLoggedIn: Boolean,
    onLoginClick: () -> Unit,
    onLogout: () -> Unit,
    profileImageUri: Uri?,
    onProfileImageSelected: (Uri?) -> Unit,
    userName: String,
    userEmail: String
) {
    // ✅ SIEMPRE MUESTRA EL PERFIL, sin importar si está logueado o no
    LoggedInProfileScreen(
        navController = navController,
        onLogout = onLogout,
        profileImageUri = profileImageUri,
        onProfileImageSelected = onProfileImageSelected,
        userName = userName,
        userEmail = userEmail,
        isLoggedIn = isLoggedIn
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoggedInProfileScreen(
    navController: NavController,
    onLogout: () -> Unit,
    profileImageUri: Uri?,
    onProfileImageSelected: (Uri?) -> Unit,
    userName: String,
    userEmail: String,
    isLoggedIn: Boolean = true
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onProfileImageSelected(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFDC040).copy(alpha = 0.1f))
                )
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFDC040).copy(alpha = 0.15f))
                        .border(4.dp, Color(0xFFFDC040).copy(alpha = 0.3f), CircleShape)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUri != null) {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = Color(0xFFFDC040),
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFDC040))
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar foto",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = userName,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = userEmail,
                fontSize = 15.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        ProfileOptionCard(
            icon = Icons.Default.ShoppingCart,
            title = "Mis Pedidos",
            iconColor = Color(0xFFFDC040),
            onClick = { navController.navigate("orders") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ProfileOptionCard(
            icon = Icons.Default.Favorite,
            title = "Favoritos",
            iconColor = Color(0xFFFDC040),
            onClick = { navController.navigate("favorites") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ProfileOptionCard(
            icon = Icons.Default.Settings,
            title = "Configuración",
            iconColor = Color(0xFFFDC040),
            onClick = { navController.navigate("settings") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ProfileOptionCard(
            icon = Icons.Default.Help,
            title = "Ayuda",
            iconColor = Color(0xFFFDC040),
            onClick = { navController.navigate("help") }
        )

        Spacer(modifier = Modifier.weight(1f))

        // ✅ Mostrar botón de logout solo si está logueado
        // Si no está logueado, puedes mostrar un botón de "Iniciar Sesión" opcional
        if (isLoggedIn) {
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFFFB800)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFFFFB800)
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFFFFB800)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cerrar Sesión",
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileOptionCard(
    icon: ImageVector,
    title: String,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}