package com.harpreet.foodbin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.harpreet.foodbin.R
import com.harpreet.foodbin.model.ItemDetail
import com.harpreet.foodbin.model.OrderItem

class OrderHistoryRecyclerAdapter(val context: Context, val orders: ArrayList<ItemDetail>) :
    RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>() {

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvItemName: TextView = view.findViewById(R.id.tvItemNameOrderHistory)
        val tvItemCost: TextView = view.findViewById(R.id.tvItemPriceOrderHistory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_order_history_single_row, parent, false)

        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val order = orders[position]
        val name = order.name
        val cost = order.cost
        holder.tvItemName.text = name
        holder.tvItemCost.text = "Rs. $cost"
    }
}