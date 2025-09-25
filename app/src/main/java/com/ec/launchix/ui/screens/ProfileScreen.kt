package com.ec.launchix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    // ✅ RECIBIR el estado de sesión desde el nivel superior
    isLoggedIn: Boolean,
    onLoginSuccess: () -> Unit,
    onLogout: () -> Unit
) {
    if (isLoggedIn) {
        // Mostrar perfil completo cuando está logueado
        LoggedInProfileScreen(
            navController = navController,
            onLogout = onLogout // ✅ Usar la función del nivel superior
        )
    } else {
        // Mostrar pantalla de inicio de sesión
        LoginPromptScreen(
            onLoginClick = onLoginSuccess // ✅ Usar la función del nivel superior
        )
    }
}

@Composable
fun LoginPromptScreen(onLoginClick: () -> Unit) {
    var showLoginForm by remember { mutableStateOf(false) }

    if (showLoginForm) {
        LoginFormScreen(
            onBackClick = { showLoginForm = false },
            onLoginSuccess = onLoginClick
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Título de la pantalla
                Text(
                    text = "Perfil",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 60.dp),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.weight(1f))

                // Icono de usuario grande
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Usuario",
                        tint = Color.Gray,
                        modifier = Modifier.size(50.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Inicia sesión",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Accede a tu cuenta para ver tu perfil",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = { showLoginForm = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Iniciar sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginFormScreen(
    onBackClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // ✅ Estados para validación de email
    var isEmailValid by remember { mutableStateOf(true) }
    var emailErrorMessage by remember { mutableStateOf("") }

    // ✅ Función para validar email
    fun validateEmail(emailText: String) {
        when {
            emailText.isEmpty() -> {
                isEmailValid = false
                emailErrorMessage = "El correo electrónico es obligatorio"
            }
            !emailText.contains("@") -> {
                isEmailValid = false
                emailErrorMessage = "El correo debe contener el símbolo @"
            }
            emailText.count { it == '@' } > 1 -> {
                isEmailValid = false
                emailErrorMessage = "El correo solo puede tener un símbolo @"
            }
            emailText.startsWith("@") -> {
                isEmailValid = false
                emailErrorMessage = "El correo no puede empezar con @"
            }
            emailText.endsWith("@") -> {
                isEmailValid = false
                emailErrorMessage = "El correo debe tener texto después del @"
            }
            else -> {
                isEmailValid = true
                emailErrorMessage = ""
            }
        }
    }

    // ✅ Función para manejar el login
    fun handleLogin() {
        validateEmail(email)
        if (isEmailValid && email.isNotEmpty() && password.isNotEmpty()) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header con botón de regreso
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Iniciar Sesión",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mensaje de bienvenida
        Text(
            text = "¡Bienvenido de vuelta!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Inicia sesión en tu cuenta para continuar",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Título del formulario
        Text(
            text = "Iniciar Sesión",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Campo de email con validación
        Text(
            text = "Correo electrónico",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                // ✅ Validar en tiempo real
                validateEmail(it)
            },
            placeholder = {
                Text(
                    text = "tu@email.com",
                    color = Color.Gray
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email",
                    tint = if (isEmailValid) Color.Gray else Color.Red
                )
            },
            // ✅ Mostrar error visual
            isError = !isEmailValid,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(12.dp),
            // ✅ Colores de error
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isEmailValid) MaterialTheme.colorScheme.primary else Color.Red,
                unfocusedBorderColor = if (isEmailValid) Color.Gray else Color.Red
            )
        )

        // ✅ Mensaje de error
        if (!isEmailValid && emailErrorMessage.isNotEmpty()) {
            Text(
                text = emailErrorMessage,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de contraseña
        Text(
            text = "Contraseña",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = {
                Text(
                    text = "Tu contraseña",
                    color = Color.Gray
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Contraseña",
                    tint = Color.Gray
                )
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                        tint = Color.Gray
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ¿Olvidaste tu contraseña?
        TextButton(
            onClick = { /* TODO: Implementar recuperación de contraseña */ },
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "¿Olvidaste tu contraseña?",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ✅ Botón de iniciar sesión con validación
        Button(
            onClick = { handleLogin() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            // ✅ Deshabilitar botón si email no es válido o campos vacíos
            enabled = isEmailValid && email.isNotEmpty() && password.isNotEmpty()
        ) {
            Text(
                text = "Iniciar Sesión",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Spacer(modifier = Modifier.height(24.dp))

        // Continuar con Google
        OutlinedButton(
            onClick = { /* TODO: Implementar login con Google */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(50.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.Gray
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "G  Continuar con Google",
                fontSize = 16.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoggedInProfileScreen(
    navController: NavController,
    onLogout: () -> Unit // ✅ Solo se llama cuando presionan "Cerrar Sesión"
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Tarjeta de perfil con gradiente azul
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2196F3) // Azul similar a la imagen
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar con círculos concéntricos
                        Box(
                            modifier = Modifier.size(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Círculo exterior
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f))
                            )
                            // Círculo interior
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Avatar",
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Mi Perfil",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "usuario@launchix.com",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ✅ Opciones del perfil CON NAVEGACIÓN
            ProfileOptionCard(
                icon = Icons.Default.ShoppingCart,
                title = "Mis Pedidos",
                iconColor = Color(0xFF2196F3),
                onClick = { navController.navigate("orders") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProfileOptionCard(
                icon = Icons.Default.Favorite,
                title = "Favoritos",
                iconColor = Color(0xFF2196F3),
                onClick = { navController.navigate("favorites") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProfileOptionCard(
                icon = Icons.Default.Settings,
                title = "Configuración",
                iconColor = Color(0xFF2196F3),
                onClick = { navController.navigate("settings") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProfileOptionCard(
                icon = Icons.Default.Help,
                title = "Ayuda",
                iconColor = Color(0xFF2196F3),
                onClick = { navController.navigate("help") }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de cerrar sesión
            OutlinedButton(
                onClick = onLogout, // ✅ Solo cierra sesión cuando presionan este botón
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF2196F3)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFF2196F3)
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFF2196F3)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cerrar Sesión",
                    fontSize = 16.sp
                )
            }
        }
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
