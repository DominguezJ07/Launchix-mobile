package com.ec.launchix.data

import java.util.Date

data class Favorite(
    val id: String,
    val userId: String,
    val productId: String,
    val productName: String,
    val productImage: String,
    val productPrice: Double,
    val productDescription: String,
    val addedDate: Date,
    val isAvailable: Boolean = true
)