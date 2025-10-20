package com.ec.launchix.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onBackClick: () -> Unit,
    onLoginSuccess: (name: String, email: String, token: String) -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isEmailValid by remember { mutableStateOf(true) }
    var emailErrorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessMessage by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val gson = Gson()

    suspend fun performLogin() {
        withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()

                val loginRequest = LoginRequest(email.trim(), password)
                val json = gson.toJson(loginRequest)
                val body = json.toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("https://launchixapi-final-production.up.railway.app/api/v1/login")
                    .post(body)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && responseBody != null) {
                        val loginResponse = gson.fromJson(responseBody, LoginResponse::class.java)
                        if (loginResponse.success) {
                            errorMessage = null
                            showSuccessMessage = true

                            // ✅ EXTRAER DATOS DEL USUARIO
                            val user = loginResponse.user ?: loginResponse.data?.user
                            val token = loginResponse.token ?: loginResponse.data?.token ?: ""

                            val userName = user?.name ?: "Usuario"
                            val userEmail = user?.email ?: email.trim()

                            // ✅ ESPERAR 1.5 SEGUNDOS PARA QUE SE VEA EL MENSAJE DE ÉXITO
                            delay(1500)

                            // ✅ DESHABILITAR LOADING ANTES DE NAVEGAR
                            isLoading = false

                            // ✅ LLAMAR AL CALLBACK PARA CAMBIAR A LA APP
                            onLoginSuccess(userName, userEmail, token)
                        } else {
                            errorMessage = loginResponse.message
                            showSuccessMessage = false
                            isLoading = false
                        }
                    } else {
                        errorMessage = "Error al iniciar sesión. Verifica tus credenciales."
                        showSuccessMessage = false
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = "Error de conexión: ${e.message}"
                    showSuccessMessage = false
                    isLoading = false
                }
            }
        }
    }

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

    fun handleLogin() {
        validateEmail(email)
        if (isEmailValid && email.isNotEmpty() && password.isNotEmpty()) {
            isLoading = true
            errorMessage = null
            showSuccessMessage = false
            scope.launch {
                performLogin()
            }
        }
    }

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
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 32.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.CenterStart),
                    enabled = !isLoading && !showSuccessMessage
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.CenterHorizontally)
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
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(55.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "¡Bienvenido de vuelta!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Inicia sesión para continuar",
                fontSize = 15.sp,
                color = Color(0xFF757575),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))

            if (showSuccessMessage) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "¡Inicio de sesión exitoso! Redirigiendo...",
                            color = Color(0xFF2E7D32),
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = Color(0xFFEF5350)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = error,
                            color = Color(0xFFD32F2F),
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Correo electrónico",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF424242),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            validateEmail(it)
                            errorMessage = null
                        },
                        placeholder = { Text("tu@email.com", color = Color(0xFFBDBDBD)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                "Email",
                                tint = if (isEmailValid) Color(0xFFFF9800) else Color(0xFFEF5350)
                            )
                        },
                        isError = !isEmailValid,
                        enabled = !isLoading && !showSuccessMessage,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF9800),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            errorBorderColor = Color(0xFFEF5350),
                            focusedContainerColor = Color(0xFFFFF3E0).copy(alpha = 0.3f),
                            unfocusedContainerColor = Color(0xFFFAFAFA)
                        )
                    )

                    if (!isEmailValid && emailErrorMessage.isNotEmpty()) {
                        Row(
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                null,
                                tint = Color(0xFFEF5350),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(emailErrorMessage, color = Color(0xFFEF5350), fontSize = 12.sp)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = "Contraseña",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF424242),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        placeholder = { Text("Tu contraseña", color = Color(0xFFBDBDBD)) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, "Contraseña", tint = Color(0xFFFF9800))
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    if (passwordVisible) "Ocultar" else "Mostrar",
                                    tint = Color(0xFF9E9E9E)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        enabled = !isLoading && !showSuccessMessage,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF9800),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color(0xFFFFF3E0).copy(alpha = 0.3f),
                            unfocusedContainerColor = Color(0xFFFAFAFA)
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    TextButton(
                        onClick = { /* TODO */ },
                        modifier = Modifier.align(Alignment.End),
                        enabled = !isLoading && !showSuccessMessage
                    ) {
                        Text(
                            "¿Olvidaste tu contraseña?",
                            color = Color(0xFFFF9800),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = { handleLogin() },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9800),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFE0E0E0)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading && !showSuccessMessage && isEmailValid && email.isNotEmpty() && password.isNotEmpty(),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Iniciar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(Modifier.weight(1f), color = Color(0xFFE0E0E0))
                        Text("o", color = Color(0xFF9E9E9E), fontSize = 13.sp, modifier = Modifier.padding(horizontal = 16.dp))
                        Divider(Modifier.weight(1f), color = Color(0xFFE0E0E0))
                    }

                    Spacer(Modifier.height(20.dp))

                    OutlinedButton(
                        onClick = { /* TODO */ },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = !isLoading && !showSuccessMessage,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF424242)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("G", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4285F4))
                            Spacer(Modifier.width(12.dp))
                            Text("Continuar con Google", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¿No tienes cuenta?", fontSize = 14.sp, color = Color(0xFF757575))
                Spacer(Modifier.width(4.dp))
                TextButton(onClick = onRegisterClick, enabled = !isLoading && !showSuccessMessage) {
                    Text(
                        "Regístrate aquí",
                        fontSize = 14.sp,
                        color = Color(0xFFFF9800),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}