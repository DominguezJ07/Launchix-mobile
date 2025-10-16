package com.ec.launchix.ui.screens.auth

import com.google.gson.annotations.SerializedName

// ✅ Modelo de Usuario (compartido)
data class UserData(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String?,
    @SerializedName("email") val email: String
)

// ========== LOGIN ==========
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("token") val token: String?,
    @SerializedName("user") val user: UserData?,
    @SerializedName("data") val data: LoginData?
)

data class LoginData(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UserData
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
    @SerializedName("message") val message: String,
    @SerializedName("token") val token: String?,
    @SerializedName("user") val user: UserData?,
    @SerializedName("data") val data: RegisterData?,
    @SerializedName("errors") val errors: Map<String, List<String>>?
)

data class RegisterData(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UserData
)