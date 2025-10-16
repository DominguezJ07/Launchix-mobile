package com.ec.launchix.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthPromptScreen(
    onLoginSuccess: () -> Unit
) {
    // ✅ CAMBIO: Usar rememberSaveable en lugar de remember
    var showLoginScreen by rememberSaveable { mutableStateOf(false) }
    var showRegisterScreen by rememberSaveable { mutableStateOf(false) }

    when {
        showLoginScreen -> {
            LoginScreen(
                onBackClick = {
                    showLoginScreen = false
                },
                onLoginSuccess = onLoginSuccess,
                onRegisterClick = {
                    showLoginScreen = false
                    showRegisterScreen = true
                }
            )
        }
        showRegisterScreen -> {
            RegisterScreen(
                // ✅ SOLUCIÓN: La flecha regresa a la pantalla de bienvenida
                onBackClick = {
                    showRegisterScreen = false
                },
                onRegisterSuccess = onLoginSuccess,
                onLoginClick = {
                    showRegisterScreen = false
                    showLoginScreen = true
                }
            )
        }
        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFFF8E1),
                                Color(0xFFFFFFFF),
                                Color(0xFFFFF3E0)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(40.dp))

                    // Header con logo o app name
                    Text(
                        text = "Launchix",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9800),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.weight(0.5f))

                    // Avatar con sombra y diseño mejorado
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .shadow(16.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFFE0B2),
                                        Color(0xFFFFCC80)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Usuario",
                            tint = Color.White,
                            modifier = Modifier.size(70.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    // Título principal
                    Text(
                        text = "Bienvenido a tu perfil",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Accede a tu cuenta para disfrutar de todas las funcionalidades",
                        fontSize = 15.sp,
                        color = Color(0xFF757575),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // Botón principal con gradiente
                    Button(
                        onClick = { showLoginScreen = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFFF9800),
                                            Color(0xFFFF6F00)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Iniciar sesión",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón secundario para registro
                    OutlinedButton(
                        onClick = { showRegisterScreen = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFFFF9800)
                        ),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFF9800)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Crear cuenta nueva",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Info adicional
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFFF3E0).copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFFFF9800),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Crea tu cuenta en segundos y comienza a disfrutar",
                                fontSize = 13.sp,
                                color = Color(0xFF424242),
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Footer
                    Text(
                        text = "Al continuar, aceptas nuestros Términos y Condiciones",
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}