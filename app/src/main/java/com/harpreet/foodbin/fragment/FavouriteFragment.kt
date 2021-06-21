package com.harpreet.foodbin.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.harpreet.foodbin.R
import com.harpreet.foodbin.activity.BaseActivity
import com.harpreet.foodbin.adapter.FavoriteRecyclerAdapter
import com.harpreet.foodbin.adapter.HomeRecyclerAdapter
import com.harpreet.foodbin.database.RestaurantDatabase
import com.harpreet.foodbin.database.RestaurantEntity

class FavouriteFragment : Fragment() {

    lateinit var recyclerFav: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var recyclerAdapter: FavoriteRecyclerAdapter
    lateinit var llDefaultLayout: LinearLayout

    var dbResataurantList = listOf<RestaurantEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        recyclerFav = view.findViewById(R.id.recyclerFavorite)
        llDefaultLayout = view.findViewById(R.id.llFavouriteDefault)

        dbResataurantList = RetrieveFavourites(activity as Context).execute().get()

        if (dbResataurantList.isEmpty()) {
            llDefaultLayout.visibility = View.VISIBLE
            recyclerFav.visibility = View.GONE
        } else if (activity != null) {

            recyclerFav.visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(context)
            recyclerAdapter = FavoriteRecyclerAdapter(context as Context, dbResataurantList)
            recyclerFav.adapter = recyclerAdapter
            recyclerFav.layoutManager = layoutManager
        }

        return view
    }

    class RetrieveFavourites(val context: Context) :
        AsyncTask<Void, Void, List<RestaurantEntity>>() {
        override fun doInBackground(vararg p0: Void?): List<RestaurantEntity> {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db")
                .build()

            return db.resDao().getAllRestaurants()
        }
    }
}