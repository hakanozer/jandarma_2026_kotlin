package com.works.myapplication.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.works.myapplication.data.remote.api.ProductApi
import com.works.myapplication.data.remote.dto.toDomain
import com.works.myapplication.domain.model.Product

class ProductPagingSource(
    private val productApi: ProductApi
) : PagingSource<Int, Product>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val position = params.key ?: 1
        return try {
            val response = productApi.getProducts(page = position, perPage = params.loadSize)
            if (response.isSuccessful) {
                val body = response.body()
                val productsDto = body?.data ?: emptyList()
                val products = productsDto.map { it.toDomain() }

                val pagination = body?.meta?.pagination
                val totalPages = pagination?.totalPages ?: 1

                LoadResult.Page(
                    data = products,
                    prevKey = if (position == 1) null else position - 1,
                    nextKey = if (position >= totalPages || products.isEmpty()) null else position + 1
                )
            } else {
                LoadResult.Error(Exception("Ürünler yüklenirken hata oluştu (${response.code()})"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}