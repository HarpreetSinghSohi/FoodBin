package com.harpreet.foodbin.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.harpreet.foodbin.R
import com.harpreet.foodbin.activity.RestaurantMenuActivity
import com.harpreet.foodbin.database.RestaurantEntity
import com.harpreet.foodbin.fragment.HomeFragment.DBAsyncTask
import com.harpreet.foodbin.model.Restaurants
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context: Context, var arrayList: ArrayList<Restaurants>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivRestaurantImage: ImageView = view.findViewById(R.id.ivRestaurantImage)
        val tvRestaurantName: TextView = view.findViewById(R.id.tvRestaurantName)
        val tvPricePerPerson: TextView = view.findViewById(R.id.tvPricePerPerson)
        val ivAddToFavorite: ImageView = view.findViewById(R.id.ivAddToFavourite)
        val tvRating: TextView = view.findViewById(R.id.tvRestaurantRating)
        val llNameCost: LinearLayout = view.findViewById(R.id.llNameCost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_row, parent, false)

        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {

        val item = arrayList[position]
        val resId = item.resId
        val resName = item.name
        holder.tvRestaurantName.text = resName
        holder.tvPricePerPerson.text = "${item.cost} / Person"
        holder.tvRating.text = item.rating
        Picasso.get().load(item.resImage).error(R.drawable.ic_drawer_big_cart)
            .into(holder.ivRestaurantImage)

        holder.llNameCost.setOnClickListener {
            openMenu(resId, resName)
        }
        holder.ivRestaurantImage.setOnClickListener {
            openMenu(resId, resName)
        }
        holder.tvRating.setOnClickListener {
            openMenu(resId, resName)
        }

        val restaurantEntity = RestaurantEntity(
            item.resId?.toInt() as Int,
            item.name,
            item.cost,
            item.rating,
            item.resImage
        )

        val checkFav = DBAsyncTask(context, restaurantEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav) {
            holder.ivAddToFavorite.setImageResource(R.drawable.ic_drawer_big_heart)
        } else {
            holder.ivAddToFavorite.setImageResource(R.drawable.ic_favorite_heart_)
        }

        holder.ivAddToFavorite.setOnClickListener {
            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async = DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    holder.ivAddToFavorite.setImageResource(R.drawable.ic_drawer_big_heart)
                } else {
                    Toast.makeText(context, "Some Error Occurred", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()
                if (result) {
                    holder.ivAddToFavorite.setImageResource(R.drawable.ic_favorite_heart_)
                } else {
                    Toast.makeText(context, "Some Error Occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getFilteredList(filteredList: List<Restaurants>) {
        arrayList = filteredList as ArrayList<Restaurants>
        notifyDataSetChanged()
    }

    fun openMenu(resId: String, resName: String) {
        val intent = Intent(context, RestaurantMenuActivity::class.java)
        intent.putExtra("res_id", resId)
        intent.putExtra("res_name", resName)
        context.startActivity(intent)
    }
}