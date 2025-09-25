package com.ec.launchix.data

import java.util.Date

data class Order(
    val id: String,
    val userId: String,
    val items: List<OrderItem>,
    val total: Double,
    val status: OrderStatus,
    val orderDate: Date,
    val deliveryAddress: String,
    val paymentMethod: String,
    val estimatedDelivery: Date? = null
)

data class OrderItem(
    val productId: String,
    val productName: String,
    val productImage: String,
    val quantity: Int,
    val price: Double,
    val subtotal: Double
)

enum class OrderStatus {
    PENDING,      // Pendiente
    CONFIRMED,    // Confirmado
    PREPARING,    // En preparación
    DELIVERING,   // En camino
    DELIVERED,    // Entregado
    CANCELLED     // Cancelado
}