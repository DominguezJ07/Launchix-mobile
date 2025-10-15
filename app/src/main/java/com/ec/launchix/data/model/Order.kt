package com.ec.launchix.data.model

data class Order(
    val id: Int,
    val ordered_at: String,
    val user_id: Int,
    val status: String
)
