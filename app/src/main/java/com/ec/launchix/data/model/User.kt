package com.ec.launchix.data.model

data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String?,
    val birthdate: String?,
    val main_address: String?,
    val city: String?,
    val postal_code: String?,
    val department: String?,
    val avatar: String?,
    val phone_verified_at: String?,
    val token: String? = null   // si tu login devuelve token
)
