package com.ec.launchix.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ec.launchix.data.Favorite
import com.ec.launchix.data.Order
import com.ec.launchix.data.OrderStatus
import com.ec.launchix.data.OrderItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class ProfileViewModel : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _favorites = MutableStateFlow<List<Favorite>>(emptyList())
    val favorites: StateFlow<List<Favorite>> = _favorites.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadOrders()
        loadFavorites()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            // Simular carga de datos - aquí conectarías con tu API/base de datos
            _orders.value = getSampleOrders()
            _isLoading.value = false
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            // Simular carga de datos - aquí conectarías con tu API/base de datos
            _favorites.value = getSampleFavorites()
            _isLoading.value = false
        }
    }

    fun removeFavorite(favoriteId: String) {
        viewModelScope.launch {
            _favorites.value = _favorites.value.filter { it.id != favoriteId }
        }
    }

    fun addToFavorites(favorite: Favorite) {
        viewModelScope.launch {
            _favorites.value = _favorites.value + favorite
        }
    }

    // Datos de ejemplo para pedidos
    private fun getSampleOrders(): List<Order> {
        return listOf(
            Order(
                id = "ORD001",
                userId = "USER001",
                items = listOf(
                    OrderItem(
                        productId = "1",
                        productName = "Pizza Margarita",
                        productImage = "https://example.com/pizza.jpg",
                        quantity = 1,
                        price = 12.99,
                        subtotal = 12.99
                    ),
                    OrderItem(
                        productId = "2",
                        productName = "Coca Cola 500ml",
                        productImage = "https://example.com/coca.jpg",
                        quantity = 2,
                        price = 2.50,
                        subtotal = 5.00
                    )
                ),
                total = 17.99,
                status = OrderStatus.DELIVERING,
                orderDate = Date(),
                deliveryAddress = "Calle 123 #45-67",
                paymentMethod = "Tarjeta de Crédito"
            ),
            Order(
                id = "ORD002",
                userId = "USER001",
                items = listOf(
                    OrderItem(
                        productId = "3",
                        productName = "Hamburguesa Clásica",
                        productImage = "https://example.com/burger.jpg",
                        quantity = 1,
                        price = 15.99,
                        subtotal = 15.99
                    )
                ),
                total = 15.99,
                status = OrderStatus.DELIVERED,
                orderDate = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_MONTH, -1)
                }.time,
                deliveryAddress = "Calle 123 #45-67",
                paymentMethod = "Efectivo"
            )
        )
    }

    // Datos de ejemplo para favoritos
    private fun getSampleFavorites(): List<Favorite> {
        return listOf(
            Favorite(
                id = "FAV001",
                userId = "USER001",
                productId = "1",
                productName = "Pizza Margarita",
                productImage = "https://example.com/pizza.jpg",
                productPrice = 12.99,
                productDescription = "Pizza tradicional con mozzarella y albahaca",
                addedDate = Date(),
                isAvailable = true
            ),
            Favorite(
                id = "FAV002",
                userId = "USER001",
                productId = "4",
                productName = "Lasagna Casera",
                productImage = "https://example.com/lasagna.jpg",
                productPrice = 18.99,
                productDescription = "Lasagna tradicional con carne y queso",
                addedDate = Date(),
                isAvailable = true
            ),
            Favorite(
                id = "FAV003",
                userId = "USER001",
                productId = "5",
                productName = "Tacos Mexicanos",
                productImage = "https://example.com/tacos.jpg",
                productPrice = 8.99,
                productDescription = "Tacos auténticos con carne y verduras",
                addedDate = Date(),
                isAvailable = false
            )
        )
    }
}