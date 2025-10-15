package com.ec.launchix.data.model

data class CartItem(
    val id: Int,
    val quantity: Int,
    val unit_price: Double,
    val cart_id: Int,
    val product_id: Int
)
