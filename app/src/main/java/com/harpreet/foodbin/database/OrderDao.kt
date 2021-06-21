package com.harpreet.foodbin.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OrderDao {

    @Insert
    fun insertItem(orderEntity: OrderEntity)

    @Delete
    fun deleteItem(orderEntity: OrderEntity)

    @Query("DELETE FROM orders")
    fun deleteAllItems()

    @Query("SELECT * FROM orders")
    fun getAllItems(): List<OrderEntity>

    @Query("SELECT * FROM orders WHERE item_Id = :itemId")
    fun getItemById(itemId: String): OrderEntity
}