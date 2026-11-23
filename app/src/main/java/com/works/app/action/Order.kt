package com.works.app.action

data class Order(
    val orderId: Int,
    val orderName: String,
    val orderPrice: Double,
    val orderCount: Int,
    val products: MutableList<Product> = mutableListOf()
)

data class Product (
    val productId: Int,
    val productName: String,
    val productPrice: Double
)
