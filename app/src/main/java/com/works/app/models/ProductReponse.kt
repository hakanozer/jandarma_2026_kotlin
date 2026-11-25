package com.works.app.models

data class ProductReponse (
    val meta: ProductMeta,
    val data: List<ProductModel>
)

data class ProductModel (
    val id: Long,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Long,
    val tags: List<String>,
    val brand: String,
    val sku: String,
    val minimumOrderQuantity: Long,
    val images: List<String>
)


data class ProductMeta (
    val status: Long,
    val message: String,
    val pagination: Pagination
)

data class Pagination (
    val page: Long,
    val per_page: Long,
    val total_items: Long,
    val total_pages: Long
)
