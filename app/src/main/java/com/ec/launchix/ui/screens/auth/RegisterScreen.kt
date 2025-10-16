package com.ec.launchix.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onRegisterSuccess: (name: String, email: String, token: String) -> Unit,
    onLoginClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isEmailValid by remember { mutableStateOf(true) }
    var emailErrorMessage by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val gson = Gson()

    suspend fun performRegister() {
        withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()

                val finalUsername = username.ifEmpty {
                    email.substringBefore("@").lowercase().replace(" ", "")
                }

                val registerRequest = RegisterRequest(
                    name = name.trim(),
                    username = finalUsername,
                    email = email.trim(),
                    password = password,
                    passwordConfirmation = confirmPassword
                )

                val json = gson.toJson(registerRequest)
                val body = json.toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("https://launchixapi-production.up.railway.app/api/v1/register")
                    .post(body)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && responseBody != null) {
                        val registerResponse = gson.fromJson(responseBody, RegisterResponse::class.java)

                        if (registerResponse.success) {
                            errorMessage = null

                            // ✅ EXTRAER DATOS DEL USUARIO
                            val user = registerResponse.user ?: registerResponse.data?.user
                            val token = registerResponse.token ?: registerResponse.data?.token ?: ""

                            val userName = user?.name ?: name.trim()
                            val userEmail = user?.email ?: email.trim()

                            // ✅ LLAMAR AL CALLBACK CON LOS DATOS
                            onRegisterSuccess(userName, userEmail, token)
                        } else {
                            errorMessage = registerResponse.errors?.values?.firstOrNull()?.firstOrNull()
                                ?: registerResponse.message
                        }
                    } else {
                        val errorResponse = try {
                            responseBody?.let { gson.fromJson(it, RegisterResponse::class.java) }
                        } catch (e: Exception) { null }

                        errorMessage = errorResponse?.errors?.values?.firstOrNull()?.firstOrNull()
                            ?: errorResponse?.message
                                    ?: "Error al registrarse. Intenta de nuevo."
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = "Error de conexión: ${e.message}"
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

    fun validatePassword() {
        when {
            password.isEmpty() -> passwordError = "La contraseña es obligatoria"
            password.length < 6 -> passwordError = "La contraseña debe tener al menos 6 caracteres"
            password != confirmPassword -> passwordError = "Las contraseñas no coinciden"
            else -> passwordError = ""
        }
    }

    fun handleRegister() {
        validateEmail(email)
        validatePassword()
        if (isEmailValid && passwordError.isEmpty() &&
            name.isNotEmpty() && email.isNotEmpty() &&
            password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            isLoading = true
            errorMessage = null
            scope.launch {
                performRegister()
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
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
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 32.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.CenterStart),
                    enabled = !isLoading
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        "Regresar",
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Crear Cuenta ✨",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Únete a nosotros hoy",
                    fontSize = 16.sp,
                    color = Color(0xFF757575)
                )
            }

            Spacer(Modifier.height(24.dp))

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
                        Icon(Icons.Default.Error, null, tint = Color(0xFFEF5350))
                        Spacer(Modifier.width(12.dp))
                        Text(error, color = Color(0xFFD32F2F), fontSize = 14.sp)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp)
                ) {
                    Spacer(Modifier.height(8.dp))

                    Text("Nombre completo", fontSize = 14.sp, fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            errorMessage = null
                        },
                        placeholder = { Text("Tu nombre", color = Color.Gray.copy(alpha = 0.5f)) },
                        leadingIcon = { Icon(Icons.Default.Person, "Nombre", tint = Color(0xFFFF9800)) },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF9800),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                            focusedContainerColor = Color(0xFFFFF3E0).copy(alpha = 0.3f),
                            unfocusedContainerColor = Color.Gray.copy(alpha = 0.03f)
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    Text("Nombre de usuario (opcional)", fontSize = 14.sp, fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it.lowercase().replace(" ", "")
                            errorMessage = null
                        },
                        placeholder = { Text("usuario123", color = Color.Gray.copy(alpha = 0.5f)) },
                        leadingIcon = { Icon(Icons.Default.AccountCircle, "Username", tint = Color(0xFFFF9800)) },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF9800),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                            focusedContainerColor = Color(0xFFFFF3E0).copy(alpha = 0.3f),
                            unfocusedContainerColor = Color.Gray.copy(alpha = 0.03f)
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    Text("Correo electrónico", fontSize = 14.sp, fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            validateEmail(it)
                            errorMessage = null
                        },
                        placeholder = { Text("tu@email.com", color = Color.Gray.copy(alpha = 0.5f)) },
                        leadingIcon = {
                            Icon(Icons.Default.Email, "Email",
                                tint = if (isEmailValid) Color(0xFFFF9800) else Color.Red)
                        },
                        isError = !isEmailValid,
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF9800),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                            errorBorderColor = Color.Red,
                            focusedContainerColor = Color(0xFFFFF3E0).copy(alpha = 0.3f),
                            unfocusedContainerColor = Color.Gray.copy(alpha = 0.03f)
                        )
                    )

                    if (!isEmailValid && emailErrorMessage.isNotEmpty()) {
                        Text(
                            emailErrorMessage,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text("Contraseña", fontSize = 14.sp, fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (confirmPassword.isNotEmpty()) validatePassword()
                            errorMessage = null
                        },
                        placeholder = { Text("Mínimo 6 caracteres", color = Color.Gray.copy(alpha = 0.5f)) },
                        leadingIcon = { Icon(Icons.Default.Lock, "Contraseña", tint = Color(0xFFFF9800)) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    if (passwordVisible) "Ocultar" else "Mostrar",
                                    tint = Color.Gray
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF9800),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                            focusedContainerColor = Color(0xFFFFF3E0).copy(alpha = 0.3f),
                            unfocusedContainerColor = Color.Gray.copy(alpha = 0.03f)
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    Text("Confirmar contraseña", fontSize = 14.sp, fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            if (password.isNotEmpty()) validatePassword()
                            errorMessage = null
                        },
                        placeholder = { Text("Repite tu contraseña", color = Color.Gray.copy(alpha = 0.5f)) },
                        leadingIcon = { Icon(Icons.Default.Lock, "Confirmar", tint = Color(0xFFFF9800)) },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    if (confirmPasswordVisible) "Ocultar" else "Mostrar",
                                    tint = Color.Gray
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = passwordError.isNotEmpty() && confirmPassword.isNotEmpty(),
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF9800),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                            errorBorderColor = Color.Red,
                            focusedContainerColor = Color(0xFFFFF3E0).copy(alpha = 0.3f),
                            unfocusedContainerColor = Color.Gray.copy(alpha = 0.03f)
                        )
                    )

                    if (passwordError.isNotEmpty() && confirmPassword.isNotEmpty()) {
                        Text(
                            passwordError,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = { handleRegister() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9800),
                            contentColor = Color.White,
                            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isLoading && name.isNotEmpty() && isEmailValid &&
                                email.isNotEmpty() && password.isNotEmpty() &&
                                confirmPassword.isNotEmpty(),
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
                            Text("Crear Cuenta", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("¿Ya tienes cuenta?", fontSize = 14.sp, color = Color.Gray)
                        TextButton(onClick = onLoginClick, enabled = !isLoading) {
                            Text(
                                "Inicia sesión",
                                fontSize = 14.sp,
                                color = Color(0xFFFF9800),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}