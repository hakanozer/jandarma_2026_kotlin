package com.works.myapplication.domain.repository

import androidx.paging.PagingData
import com.works.myapplication.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProductsStream(): Flow<PagingData<Product>>
}