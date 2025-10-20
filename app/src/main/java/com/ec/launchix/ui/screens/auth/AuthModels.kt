package com.ec.launchix.ui.screens.auth

import com.google.gson.annotations.SerializedName


// ✅ Modelo de Usuario (compartido)
data class UserData(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("email") val email: String? = null
)

// ========== LOGIN ==========
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("token") val token: String? = null,
    @SerializedName("user") val user: UserData? = null,
    @SerializedName("data") val data: LoginData? = null
)

data class LoginData(
    @SerializedName("token") val token: String? = null,
    @SerializedName("user") val user: UserData? = null
)

// ========== REGISTER ==========
data class RegisterRequest(
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String
)

data class RegisterResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("token") val token: String? = null,
    @SerializedName("user") val user: UserData? = null,
    @SerializedName("data") val data: RegisterData? = null,
    @SerializedName("errors") val errors: Map<String, List<String>>? = null
)

data class RegisterData(
    @SerializedName("token") val token: String? = null,
    @SerializedName("user") val user: UserData? = null
)