package com.harpreet.foodbin.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.harpreet.foodbin.R
import com.harpreet.foodbin.adapter.CartRecyclerAdapter
import com.harpreet.foodbin.database.OrderEntity
import com.harpreet.foodbin.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_base.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    lateinit var tvResName: TextView
    lateinit var recyclerCartView: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var btnPlaceOrder: Button
    lateinit var toolbar: androidx.appcompat.widget.Toolbar


    lateinit var sharedPreferences: SharedPreferences

    var dbItemList = listOf<OrderEntity>()
    lateinit var resName: String
    lateinit var resId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_cart)


        tvResName = findViewById(R.id.tvTextResName)
        recyclerCartView = findViewById(R.id.recyclerCartItems)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        toolbar = findViewById(R.id.toolBarCart)
        resName = intent.getStringExtra("resName")
        resId = intent.getStringExtra("resId")

        tvResName.text = "Ordered From : ${resName}"
        val userid = sharedPreferences.getString("userId", "0")
        dbItemList = RestaurantMenuActivity.RetrieveItems(this as Context).execute().get()
        val itemIds = JSONArray()
        for (i in dbItemList) {
            val itemObject = JSONObject()
            itemObject.put("food_item_id", i.item_Id)
            itemIds.put(itemObject)
        }
        var totalCost = 0
        for (i in dbItemList) {
            totalCost += i.itemCost.toInt()
        }
        btnPlaceOrder.text = "Place Order(Total: Rs. $totalCost )"
        setToolBar()

        layoutManager = LinearLayoutManager(this as Context)
        recyclerAdapter = CartRecyclerAdapter(this as Context, dbItemList)
        recyclerCartView.adapter = recyclerAdapter
        recyclerCartView.layoutManager = layoutManager

        val queue = Volley.newRequestQueue(this as Context)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"
        for (i in 0 until itemIds.length()) {
            val itObj = itemIds.getJSONObject(i)
        }
        val jsonParam = JSONObject()
        jsonParam.put("user_id", userid)
        jsonParam.put("restaurant_id", resId)
        jsonParam.put("total_cost", totalCost.toString())
        jsonParam.put("food", itemIds)
        btnPlaceOrder.setOnClickListener {

            if (ConnectionManager().checkConnectivity(this as Context)) {
                val jsonObjectRequest =
                    object : JsonObjectRequest(Method.POST, url, jsonParam, Response.Listener {

                        try {

                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success) {
                                setResult(Activity.RESULT_OK, Intent())
                                RestaurantMenuActivity.DeleteAllItems(this as Context)
                                finish()

                            } else {
                                Toast.makeText(
                                    this as Context,
                                    "Order not placed as some error has occurred!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } catch (e: JSONException) {
                            Toast.makeText(
                                this as Context,
                                "Some error has occurred!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }, Response.ErrorListener {
                        Toast.makeText(
                            this as Context,
                            "Some error has occurred!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val header = HashMap<String, String>()
                            header["Content-type"] = "application/json"
                            header["token"] = "0944c7ec0831b0"
                            return header
                        }
                    }
                queue.add(jsonObjectRequest)
                RestaurantMenuActivity.DeleteAllItems(this as Context).execute().get()
            } else {
                showDialog()
            }
        }
    }

    fun setToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun showDialog() {
        if (!ConnectionManager().checkConnectivity(this as Context)) {
            val dialog = AlertDialog.Builder(this as Context)
            dialog.setTitle(R.string.no_internet_connection_error)
            dialog.setMessage(R.string.no_internet_connection_message)
            dialog.setPositiveButton(getString(R.string.retry)) { text, listener ->
                this?.finishAffinity()
            }
            dialog.create()
            dialog.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
