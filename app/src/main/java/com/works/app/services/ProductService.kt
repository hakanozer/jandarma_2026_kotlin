package com.works.app.services

import com.works.app.models.ProductReponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductService {

    // products?page=1&per_page=10
    @GET("products")
    fun allProducts(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 10
    ) : Call<ProductReponse>

}