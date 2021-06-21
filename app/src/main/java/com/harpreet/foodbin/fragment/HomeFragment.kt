package com.harpreet.foodbin.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.harpreet.foodbin.R
import com.harpreet.foodbin.adapter.HomeRecyclerAdapter
import com.harpreet.foodbin.database.RestaurantDatabase
import com.harpreet.foodbin.database.RestaurantEntity
import com.harpreet.foodbin.model.Restaurants
import com.harpreet.foodbin.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class HomeFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var searchBar: EditText

    val resList = arrayListOf<Restaurants>()


    var ratingComparator = Comparator<Restaurants> { res1, res2 ->
        if (res1.rating.compareTo(res2.rating, true) == 0) {
            res1.name.compareTo(res2.name, true)
        } else {
            res1.rating.compareTo(res2.rating, true)
        }
    }
    var priceComparator = Comparator<Restaurants> { res1, res2 ->
        if (res1.cost.compareTo(res2.cost, true) == 0) {
            res1.name.compareTo(res2.name, true)
        } else {
            res1.cost.compareTo(res2.cost, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        recyclerHome = view.findViewById(R.id.recyclerHome)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progresBar)
        searchBar = view.findViewById(R.id.etSearchBar)

        progressLayout.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                filter(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {

                    try {
                        progressLayout.visibility = View.GONE

                        val firstJsonObject = it.getJSONObject("data")
                        val success = firstJsonObject.getBoolean("success")

                        if (success) {
                            val data = firstJsonObject.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val resJsonObject = data.getJSONObject(i)
                                val resObject = Restaurants(
                                    resJsonObject.getString("id"),
                                    resJsonObject.getString("name"),
                                    resJsonObject.getString("rating"),
                                    resJsonObject.getString("cost_for_one"),
                                    resJsonObject.getString("image_url")
                                )
                                resList.add(resObject)
                                layoutManager = LinearLayoutManager(activity)
                                recyclerAdapter = HomeRecyclerAdapter(activity as Context, resList)
                                recyclerHome.adapter = recyclerAdapter
                                recyclerHome.layoutManager = layoutManager
                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error has occurred.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Some error occurred. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val header = HashMap<String, String>()
                        header["Content-type"] = "application/json"
                        header["token"] = "0944c7ec0831b0"
                        return header
                    }
                }
            queue.add(jsonObjectRequest)
        } else {
            showDialog()
        }

        return view
    }

    private fun filter(p0: String) {

        val resListFilter = arrayListOf<Restaurants>()
        for (i in resList) {
            if (i.name.toLowerCase().contains(p0.toLowerCase())) {
                resListFilter.add(i)
            }
        }
        recyclerAdapter.getFilteredList(resListFilter)

    }

    fun showDialog() {
        if (!ConnectionManager().checkConnectivity(activity as Context)) {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle(R.string.no_internet_connection_error)
            dialog.setMessage(R.string.no_internet_connection_message)
            dialog.setPositiveButton(getString(R.string.retry)) { text, listener ->
                activity?.finishAffinity()
            }
            dialog.create()
            dialog.show()
        }
    }

    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        /*
            mode 1 -> check db if restaurant is favourite or not
            mode 2 -> save restaurant into favourite
            mode 3 -> remove restaurant from favourite
             */

        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when (mode) {

                1 -> {
                    val restaurant: RestaurantEntity? =
                        db.resDao().getRestaurantById(restaurantEntity.res_Id.toString())
                    db.close()
                    return restaurant != null
                }

                2 -> {
                    db.resDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.resDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item?.itemId

        if (id == R.id.action_sort) {
            dialog()
        }
        return super.onOptionsItemSelected(item)
    }

    fun dialog() {
        val options = arrayOf("Cost(Low to High)", "Cost(High to Low)", "Rating")
        var selectedItem = 0
        val builder = AlertDialog.Builder(context as Context)
        builder.setTitle("Sort By?")
        builder.setSingleChoiceItems(
            options, 4

        ) { dialogInterface: DialogInterface, item: Int ->
            selectedItem = item
        }
        builder.setPositiveButton(R.string.ok) { dialogInterface: DialogInterface, p1: Int ->
            if (selectedItem == 0) {
                Collections.sort(resList, priceComparator)

            } else if (selectedItem == 1) {
                Collections.sort(resList, priceComparator)
                resList.reverse()

            } else if (selectedItem == 2) {
                Collections.sort(resList, ratingComparator)
                resList.reverse()
            }
            recyclerAdapter.notifyDataSetChanged()
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(R.string.cancel) { dialogInterface: DialogInterface, p1: Int ->
            dialogInterface.dismiss()
        }
        builder.create()
        builder.show();
    }

    override fun onResume() {
        if (!ConnectionManager().checkConnectivity(context as Context)) {
            showDialog()
        }
        super.onResume()
    }
}