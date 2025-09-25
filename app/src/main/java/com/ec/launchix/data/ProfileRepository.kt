package com.ec.launchix.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

class ProfileRepository {

    // Simular base de datos local con listas mutables
    private val _orders = mutableListOf<Order>()
    private val _favorites = mutableListOf<Favorite>()

    init {
        // Inicializar con datos de ejemplo
        initializeSampleData()
    }

    // Operaciones de Pedidos
    suspend fun getOrders(userId: String): Flow<List<Order>> = flow {
        delay(500) // Simular latencia de red
        emit(_orders.filter { it.userId == userId })
    }

    suspend fun getOrderById(orderId: String): Order? {
        delay(200)
        return _orders.find { it.id == orderId }
    }

    suspend fun addOrder(order: Order) {
        delay(300)
        _orders.add(order)
    }

    // Operaciones de Favoritos
    suspend fun getFavorites(userId: String): Flow<List<Favorite>> = flow {
        delay(300)
        emit(_favorites.filter { it.userId == userId })
    }

    suspend fun addToFavorites(favorite: Favorite) {
        delay(200)
        // Verificar si ya existe
        if (_favorites.none { it.productId == favorite.productId && it.userId == favorite.userId }) {
            _favorites.add(favorite)
        }
    }

    suspend fun removeFromFavorites(userId: String, productId: String) {
        delay(200)
        _favorites.removeAll { it.userId == userId && it.productId == productId }
    }

    suspend fun isFavorite(userId: String, productId: String): Boolean {
        delay(100)
        return _favorites.any { it.userId == userId && it.productId == productId }
    }

    private fun initializeSampleData() {
        // Datos de ejemplo para pedidos
        _orders.addAll(listOf(
            Order(
                id = "ORD001",
                userId = "usuario@launchix.com",
                items = listOf(
                    OrderItem(
                        productId = "1",
                        productName = "Pizza Margarita Grande",
                        productImage = "https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=300",
                        quantity = 1,
                        price = 18.99,
                        subtotal = 18.99
                    ),
                    OrderItem(
                        productId = "2",
                        productName = "Bebida Coca Cola 500ml",
                        productImage = "https://images.unsplash.com/photo-1561758033-d89a9ad46330?w=300",
                        quantity = 2,
                        price = 2.50,
                        subtotal = 5.00
                    )
                ),
                total = 23.99,
                status = OrderStatus.DELIVERING,
                orderDate = Date(),
                deliveryAddress = "Calle 5 #12-34, Popayán",
                paymentMethod = "Tarjeta de Crédito",
                estimatedDelivery = Calendar.getInstance().apply {
                    add(Calendar.MINUTE, 25)
                }.time
            ),
            Order(
                id = "ORD002",
                userId = "usuario@launchix.com",
                items = listOf(
                    OrderItem(
                        productId = "3",
                        productName = "Hamburguesa Artesanal",
                        productImage = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=300",
                        quantity = 1,
                        price = 15.99,
                        subtotal = 15.99
                    ),
                    OrderItem(
                        productId = "4",
                        productName = "Papas Fritas Grandes",
                        productImage = "https://images.unsplash.com/photo-1576107232684-1279f390859f?w=300",
                        quantity = 1,
                        price = 4.99,
                        subtotal = 4.99
                    )
                ),
                total = 20.98,
                status = OrderStatus.DELIVERED,
                orderDate = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_MONTH, -1)
                }.time,
                deliveryAddress = "Calle 5 #12-34, Popayán",
                paymentMethod = "Efectivo"
            ),
            Order(
                id = "ORD003",
                userId = "usuario@launchix.com",
                items = listOf(
                    OrderItem(
                        productId = "5",
                        productName = "Sushi Variado",
                        productImage = "https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=300",
                        quantity = 1,
                        price = 25.99,
                        subtotal = 25.99
                    )
                ),
                total = 25.99,
                status = OrderStatus.CANCELLED,
                orderDate = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_MONTH, -3)
                }.time,
                deliveryAddress = "Calle 5 #12-34, Popayán",
                paymentMethod = "Tarjeta de Débito"
            )
        ))

        // Datos de ejemplo para favoritos
        _favorites.addAll(listOf(
            Favorite(
                id = "FAV001",
                userId = "usuario@launchix.com",
                productId = "1",
                productName = "Pizza Margarita Grande",
                productImage = "https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=300",
                productPrice = 18.99,
                productDescription = "Pizza tradicional italiana con mozzarella fresca, tomate y albahaca",
                addedDate = Date(),
                isAvailable = true
            ),
            Favorite(
                id = "FAV002",
                userId = "usuario@launchix.com",
                productId = "6",
                productName = "Lasagna Casera",
                productImage = "https://images.unsplash.com/photo-1621996346565-e3dbc353d2e5?w=300",
                productPrice = 16.99,
                productDescription = "Lasagna tradicional con carne molida, salsa bechamel y queso",
                addedDate = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_MONTH, -2)
                }.time,
                isAvailable = true
            ),
            Favorite(
                id = "FAV003",
                userId = "usuario@launchix.com",
                productId = "7",
                productName = "Tacos Mexicanos",
                productImage = "https://images.unsplash.com/photo-1565299585323-38174c4a6706?w=300",
                productPrice = 12.99,
                productDescription = "Tacos auténticos con carne de res, cebolla, cilantro y salsa",
                addedDate = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_MONTH, -5)
                }.time,
                isAvailable = false
            ),
            Favorite(
                id = "FAV004",
                userId = "usuario@launchix.com",
                productId = "8",
                productName = "Ensalada César",
                productImage = "https://images.unsplash.com/photo-1546793665-c74683f339c1?w=300",
                productPrice = 8.99,
                productDescription = "Ensalada fresca con lechuga romana, crotones, parmesano y aderezo césar",
                addedDate = Calendar.getInstance().apply {
                    add(Calendar.HOUR, -12)
                }.time,
                isAvailable = true
            )
        ))
    }
}