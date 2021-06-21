package com.harpreet.foodbin.model

data class MenuItems(
    val itemId: String,
    val itemName: String,
    val itemCost: String,
    val restaurantId: String,
    val restaurantName: String
)