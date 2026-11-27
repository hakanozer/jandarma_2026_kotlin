package com.works.myapplication.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.works.myapplication.domain.model.Product

data class ProductListResponse(
    @SerializedName("meta") val meta: ProductMetaDto,
    @SerializedName("data") val data: List<ProductDto>?
)

data class ProductMetaDto(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("pagination") val pagination: PaginationDto?
)

data class PaginationDto(
    @SerializedName("page") val page: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("total_items") val totalItems: Int,
    @SerializedName("total_pages") val totalPages: Int
)

data class ProductDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("price") val price: Double?,
    @SerializedName("discountPercentage") val discountPercentage: Double?,
    @SerializedName("rating") val rating: Double?,
    @SerializedName("stock") val stock: Int?,
    @SerializedName("tags") val tags: List<String>?,
    @SerializedName("brand") val brand: String?,
    @SerializedName("sku") val sku: String?,
    @SerializedName("minimumOrderQuantity") val minimumOrderQuantity: Int?,
    @SerializedName("images") val images: List<String>?
)

fun ProductDto.toDomain(): Product {
    return Product(
        id = id,
        title = title ?: "",
        description = description ?: "",
        category = category ?: "",
        price = price ?: 0.0,
        discountPercentage = discountPercentage ?: 0.0,
        rating = rating ?: 0.0,
        stock = stock ?: 0,
        tags = tags ?: emptyList(),
        brand = brand ?: "",
        sku = sku ?: "",
        minimumOrderQuantity = minimumOrderQuantity ?: 1,
        images = images ?: emptyList()
    )
}