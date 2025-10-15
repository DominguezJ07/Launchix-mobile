package com.ec.launchix.data.repository

import com.ec.launchix.data.network.ApiClient
import com.ec.launchix.data.network.ApiService
import com.ec.launchix.data.network.LoginRequest

class UserRepository {
    private val api = ApiClient.retrofit.create(ApiService::class.java)

    suspend fun login(email: String, password: String) =
        api.login(LoginRequest(email, password))

    suspend fun getProducts() = api.getProducts()

    suspend fun getUserProfile() = api.getUserProfile()
}
