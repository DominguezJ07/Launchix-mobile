package com.ec.launchix.data.repository

import com.ec.launchix.data.model.Product
import com.ec.launchix.data.network.ApiClient
import retrofit2.Response

class ProductRepository {

    suspend fun getProducts(): Response<List<Product>> {
        return ApiClient.apiService.getProducts()
    }


}
