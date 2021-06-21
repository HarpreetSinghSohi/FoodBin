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
import com.harpreet.foodbin.fragment.HomeFragment
import com.squareup.picasso.Picasso

class FavoriteRecyclerAdapter(val context: Context, val resList: List<RestaurantEntity>) :
    RecyclerView.Adapter<FavoriteRecyclerAdapter.FavoriteViewHolder>() {

    class FavoriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivRestaurantImage: ImageView = view.findViewById(R.id.ivRestaurantImage)
        val tvRestaurantName: TextView = view.findViewById(R.id.tvRestaurantName)
        val tvPricePerPerson: TextView = view.findViewById(R.id.tvPricePerPerson)
        val ivAddToFavorite: ImageView = view.findViewById(R.id.ivAddToFavourite)
        val tvRating: TextView = view.findViewById(R.id.tvRestaurantRating)
        val llNameCost: LinearLayout = view.findViewById(R.id.llNameCost)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteRecyclerAdapter.FavoriteViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_row, parent, false)

        return FavoriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return resList.size
    }

    override fun onBindViewHolder(
        holder: FavoriteRecyclerAdapter.FavoriteViewHolder,
        position: Int
    ) {

        val restaurant = resList[position]
        val resId = restaurant.res_Id.toString()
        val resName: String = restaurant.resName
        holder.tvRestaurantName.text = resName
        holder.tvPricePerPerson.text = "${restaurant.resCost} / Person"
        holder.tvRating.text = restaurant.resRating
        Picasso.get().load(restaurant.resImage).error(R.drawable.ic_drawer_big_cart)
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
            restaurant.res_Id?.toInt() as Int,
            restaurant.resName,
            restaurant.resCost,
            restaurant.resRating,
            restaurant.resImage
        )

        val checkFav = HomeFragment.DBAsyncTask(context, restaurantEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav) {
            holder.ivAddToFavorite.setImageResource(R.drawable.ic_drawer_big_heart)
        } else {
            holder.ivAddToFavorite.setImageResource(R.drawable.ic_favorite_heart_)
        }

        holder.ivAddToFavorite.setOnClickListener {
            if (!HomeFragment.DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async = HomeFragment.DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    holder.ivAddToFavorite.setImageResource(R.drawable.ic_drawer_big_heart)
                } else {
                    Toast.makeText(context, "Some Error Occurred", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = HomeFragment.DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()
                if (result) {
                    holder.ivAddToFavorite.setImageResource(R.drawable.ic_favorite_heart_)
                } else {
                    Toast.makeText(context, "Some Error Occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun openMenu(id: String, name: String) {
        val intent = Intent(context, RestaurantMenuActivity::class.java)
        intent.putExtra("res_id", id)
        intent.putExtra("res_name", name)
        context.startActivity(intent)

    }
}