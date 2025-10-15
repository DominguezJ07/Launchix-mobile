package com.ec.launchix.data.model

data class Product(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val sales: Int?,
    val entrepreneur_id: Int?,
    val user_id: Int?
)
