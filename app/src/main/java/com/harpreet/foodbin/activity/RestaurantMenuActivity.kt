package com.harpreet.foodbin.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.harpreet.foodbin.R
import com.harpreet.foodbin.adapter.MenuRecyclerAdapter
import com.harpreet.foodbin.database.OrderDatabase
import com.harpreet.foodbin.database.OrderEntity
import com.harpreet.foodbin.model.MenuItems
import com.harpreet.foodbin.util.ConnectionManager
import org.json.JSONException


class RestaurantMenuActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var btnProceedToCart: Button
    lateinit var recyclerMenuItem: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var recyclerAdapter: MenuRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var fa: Activity


    val itemList = arrayListOf<MenuItems>()
    var dbItemList = listOf<OrderEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)

        initializeIds()
        setUpToolBar()
        progressLayout.visibility = View.VISIBLE
        val resId = intent.getStringExtra("res_id")
        val resName = intent.getStringExtra("res_name")
        supportActionBar?.title = resName
        val queue = Volley.newRequestQueue(this as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/${resId}"

        if (ConnectionManager().checkConnectivity(this as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {

                    try {
                        progressLayout.visibility = View.GONE

                        val firstJsonObject = it.getJSONObject("data")
                        val success = firstJsonObject.getBoolean("success")

                        if (success) {
                            val data = firstJsonObject.getJSONArray("data")
                            for (i in 0 until data.length()) {

                                val itemJsonObject = data.getJSONObject(i)
                                val itemObject = MenuItems(
                                    itemJsonObject.getString("id"),
                                    itemJsonObject.getString("name"),
                                    itemJsonObject.getString("cost_for_one"),
                                    itemJsonObject.getString("restaurant_id"),
                                    resName
                                )
                                itemList.add(itemObject)
                                layoutManager = LinearLayoutManager(this)
                                recyclerAdapter = MenuRecyclerAdapter(this as Context, itemList)
                                recyclerMenuItem.adapter = recyclerAdapter
                                recyclerMenuItem.layoutManager = layoutManager
                            }
                        } else {
                            Toast.makeText(
                                this as Context,
                                "Some Error has occured.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {

                        Toast.makeText(
                            this as Context,
                            "Some unexpected error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }, Response.ErrorListener {

                    if (this != null) {
                        Toast.makeText(
                            this as Context,
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


        btnProceedToCart.setOnClickListener {
            dbItemList = RetrieveItems(this as Context).execute().get()
            if (dbItemList.isEmpty()) {
                Toast.makeText(
                    this as Context,
                    "Select at least one food item to proceed further.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val intent = Intent(this@RestaurantMenuActivity, CartActivity::class.java)
                intent.putExtra("resId", resId)
                intent.putExtra("resName", resName)
                startActivityForResult(intent, 1)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            startActivity(Intent(this, OrderedPlacedActivity::class.java))
            finish()
        }
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

    private fun initializeIds() {
        toolbar = findViewById(R.id.toolBarMenu)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        btnProceedToCart = findViewById(R.id.btnProceedToCart)
        recyclerMenuItem = findViewById(R.id.recyclerMenuItem)
    }

    fun setUpToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {

        dbItemList = RetrieveItems(this as Context).execute().get()
        if (!dbItemList.isEmpty()) {
            val dialog = AlertDialog.Builder(this@RestaurantMenuActivity)
            dialog.setTitle(R.string.confirmation)
            dialog.setMessage(R.string.going_back_menu)
            dialog.setPositiveButton(getString(R.string.yes)) { text, listener ->
                DeleteAllItems(this as Context).execute().get()
                super.onBackPressed()
            }
            dialog.setNegativeButton(getString(R.string.no)) { text, listener ->
            }
            dialog.create()
            dialog.show()
        } else {
            super.onBackPressed()
        }

    }

    class DBAsyncTask(val context: Context, val orderEntity: OrderEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        /*
            mode 1 -> check db if item is added to cart or not
            mode 2 -> add item into cart
            mode 3 -> remove item from cart
             */
        val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when (mode) {

                1 -> {

                    val order: OrderEntity =
                        db.itemDao().getItemById(orderEntity.item_Id.toString())
                    db.close()
                    return order != null
                }

                2 -> {
                    db.itemDao().insertItem(orderEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.itemDao().deleteItem(orderEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }

    class RetrieveItems(val context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {
        val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()
        override fun doInBackground(vararg p0: Void?): List<OrderEntity> {
            return db.itemDao().getAllItems()
        }
    }

    class DeleteAllItems(val context: Context) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean {
            db.itemDao().deleteAllItems()
            return true
        }
    }
}

