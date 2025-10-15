package com.ec.launchix.data.model

data class Favorite(
    val id: Int,
    val user_id: Int,
    val favoritable_id: Int,
    val favoritable_type: String
)
