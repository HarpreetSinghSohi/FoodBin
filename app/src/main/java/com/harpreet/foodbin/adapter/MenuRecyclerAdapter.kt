package com.harpreet.foodbin.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.harpreet.foodbin.R
import com.harpreet.foodbin.activity.RestaurantMenuActivity
import com.harpreet.foodbin.database.OrderEntity
import com.harpreet.foodbin.model.MenuItems

class MenuRecyclerAdapter(val context: Context, val menuItemsList: ArrayList<MenuItems>) :
    RecyclerView.Adapter<MenuRecyclerAdapter.MenuViewHolder>() {

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvItemCount: TextView = view.findViewById(R.id.tvItemCount)
        val tvItemName: TextView = view.findViewById(R.id.tvItemName)
        val tvItemCost: TextView = view.findViewById(R.id.tvItemPrice)
        val btnAddRemoveItem: Button = view.findViewById(R.id.btnAddRemoveItem)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_menu_single_row, parent, false)

        return MenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menuItemsList.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {

        val item = menuItemsList[position]
        val resId = item.restaurantId
        val resName = item.restaurantName
        holder.tvItemCount.text = (position + 1).toString()
        holder.tvItemName.text = item.itemName
        holder.tvItemCost.text = item.itemCost

        val orderEntity = OrderEntity(
            item.itemId.toInt() as Int,
            item.itemName,
            item.itemCost
        )

        val checkCart =
            RestaurantMenuActivity.DBAsyncTask(context as Context, orderEntity, 1).execute()
        val inCart = checkCart.get()

        if (inCart) {
            holder.btnAddRemoveItem.text = context.getString(R.string.remove)
            holder.btnAddRemoveItem.setBackgroundColor(context.getColor(R.color.yellow))
        } else {
            holder.btnAddRemoveItem.text = context.getString(R.string.add)
            holder.btnAddRemoveItem.setBackgroundColor(context.getColor(R.color.colorPrimary))
        }

        holder.btnAddRemoveItem.setOnClickListener {


            if (!RestaurantMenuActivity.DBAsyncTask(context, orderEntity, 1).execute().get()) {
                val async = RestaurantMenuActivity.DBAsyncTask(context, orderEntity, 2).execute()
                val result = async.get()
                if (result) {
                    holder.btnAddRemoveItem.text = context.getString(R.string.remove)
                    holder.btnAddRemoveItem.setBackgroundColor(context.getColor(R.color.yellow))
                }
            } else {
                val async = RestaurantMenuActivity.DBAsyncTask(context, orderEntity, 3).execute()
                val result = async.get()
                if (result) {
                    holder.btnAddRemoveItem.text = context.getString(R.string.add)
                    holder.btnAddRemoveItem.setBackgroundColor(context.getColor(R.color.colorPrimary))
                }
            }

        }
    }
}