package com.ec.launchix.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ec.launchix.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val repo = UserRepository()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val response = repo.login(email, password)
            if (response.isSuccessful) {
                val user = response.body()
                // ✅ manejar login correcto
            } else {
                // ❌ manejar error
            }
        }
    }

    fun loadProducts() {
        viewModelScope.launch {
            val response = repo.getProducts()
            if (response.isSuccessful) {
                val products = response.body()
                // ✅ mostrar productos
            }
        }
    }
}
