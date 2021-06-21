package com.harpreet.foodbin.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val item_Id: Int,
    @ColumnInfo(name = "item_name") val itemName: String,
    @ColumnInfo(name = "item_cost") val itemCost: String
)