package com.works.app.pages

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.works.app.R
import com.works.app.models.ProductSingleReponse
import com.works.app.services.ProductService
import com.works.app.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val id = intent.getLongExtra("id", 0)
        if (id > 0) {
            val api = RetrofitClient.apiService.create(ProductService::class.java)
            api.getSingleProduct(id).enqueue(object : Callback<ProductSingleReponse> {
                override fun onResponse(
                    call: Call<ProductSingleReponse?>,
                    response: Response<ProductSingleReponse?>
                ) {
                    if (response.isSuccessful) {
                        val item = response.body()?.data
                        if (item != null)
                        Log.d("TAG", "item: " + item)
                    }
                }

                override fun onFailure(
                    call: Call<ProductSingleReponse?>,
                    t: Throwable
                ) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

}