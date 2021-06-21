package com.harpreet.foodbin.model

import org.json.JSONArray

data class OrderItem(
    val orderId: String,
    val resName: String,
    val totalCost: String,
    val dateTime: String,
    val itemDetail: JSONArray
)