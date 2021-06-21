package com.harpreet.foodbin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.harpreet.foodbin.R
import com.harpreet.foodbin.model.ItemDetail
import com.harpreet.foodbin.model.OrderItem

class OrderHistoryUpperRecyclerAdapter(val context: Context, val orders: ArrayList<OrderItem>) :
    RecyclerView.Adapter<OrderHistoryUpperRecyclerAdapter.OrderHistoryUpperViewHolder>() {

    class OrderHistoryUpperViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvResName: TextView = view.findViewById(R.id.tvResNameOrderHistory)
        val tvOrderDate: TextView = view.findViewById(R.id.tvDateOrderHistory)
        val tvTotalCost: TextView = view.findViewById(R.id.tvTotalCostOrderHistory)
        val recyclerViewInner: RecyclerView = view.findViewById(R.id.recyclerOrderHistoryUpper)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryUpperViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_order_history_res_name, parent, false)

        return OrderHistoryUpperViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: OrderHistoryUpperViewHolder, position: Int) {
        val order = orders[position]
        val name = order.resName
        val orderDate = order.dateTime
        var itemList = arrayListOf<ItemDetail>()


        holder.tvResName.text = name
        holder.tvOrderDate.text = orderDate.substring(0, 9)
        holder.tvTotalCost.text = "Rs. ${order.totalCost}"

        val itemJsonarray = order.itemDetail
        for (j in 0 until itemJsonarray.length()) {
            val itemJsonObject = itemJsonarray.getJSONObject(j)
            val itemObject = ItemDetail(
                itemJsonObject.getString("food_item_id"),
                itemJsonObject.getString("name"),
                itemJsonObject.getString("cost")
            )
            itemList.add(itemObject)
        }
        val innerAdapter = OrderHistoryRecyclerAdapter(context, itemList)
        val linearLayoutManager = LinearLayoutManager(context)
        holder.recyclerViewInner.layoutManager = linearLayoutManager
        holder.recyclerViewInner.adapter = innerAdapter
    }
}