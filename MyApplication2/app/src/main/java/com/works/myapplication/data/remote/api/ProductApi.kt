package com.works.myapplication.data.remote.api

import com.works.myapplication.data.remote.dto.ProductListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ProductApi {
    @Headers("Accept: application/json")
    @GET("api/products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10
    ): Response<ProductListResponse>
}