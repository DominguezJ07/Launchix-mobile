package com.ec.launchix.data.network

import com.ec.launchix.data.model.User
import com.ec.launchix.data.model.Product
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class LoginRequest(
    val email: String,
    val password: String
)

interface ApiService {

    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<User>

    @GET("products")
    suspend fun getProducts(): Response<List<Product>>

    @GET("user/profile")
    suspend fun getUserProfile(): Response<User>
}
