package com.ec.launchix.data.repository

import com.ec.launchix.data.model.Product
import com.ec.launchix.data.network.ApiClient
import retrofit2.Response

class ProductRepository {

    suspend fun getProducts(): Response<List<Product>> {
        return ApiClient.apiService.getProducts()
    }

    suspend fun getProductById(id: Int): Response<Product> {
        return ApiClient.apiService.getProductById(id)
    }

    suspend fun getPopularProducts(): Response<List<Product>> {
        return ApiClient.apiService.getPopularProducts()
    }

    suspend fun getFeaturedProducts(): Response<List<Product>> {
        return ApiClient.apiService.getFeaturedProducts()
    }
}
