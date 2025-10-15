package com.ec.launchix.ui.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.ec.launchix.R
import com.ec.launchix.viewmodels.ProductViewModel

class ProductActivity : AppCompatActivity() {

    private val productViewModel: ProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        productViewModel.products.observe(this, Observer { productList ->
            println("✅ Productos recibidos: $productList")
        })

        productViewModel.error.observe(this, Observer { error ->
            error?.let {
                println("❌ Error: $it")
            }
        })

        productViewModel.isLoading.observe(this, Observer { loading ->
            println("⏳ Cargando: $loading")
        })
    }
}
