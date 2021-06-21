package com.harpreet.foodbin.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.harpreet.foodbin.R
import com.harpreet.foodbin.adapter.OrderHistoryUpperRecyclerAdapter
import com.harpreet.foodbin.model.ItemDetail
import com.harpreet.foodbin.model.OrderItem
import com.harpreet.foodbin.util.ConnectionManager
import org.json.JSONException

class OrderHistoryFragment : Fragment() {

    lateinit var recyclerOrderHistory: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var recyclerAdapterUpper: OrderHistoryUpperRecyclerAdapter
    lateinit var llOrderhistorydefault: LinearLayout
    lateinit var rlOrderHistoryDisplay: RelativeLayout

    lateinit var sharedPreferences: SharedPreferences
    var orderList = arrayListOf<OrderItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        rlOrderHistoryDisplay = view.findViewById(R.id.rlOrderHistoryDisplay)
        llOrderhistorydefault = view.findViewById(R.id.llOrderHistoryDefault)
        recyclerOrderHistory = view.findViewById(R.id.recyclerOrderHistoryGroup)

        rlOrderHistoryDisplay.visibility = View.GONE
        val user_id = sharedPreferences.getString("userId", "0")
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/${user_id}"

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {

                    try {

                        val firstJsonObject = it.getJSONObject("data")
                        val success = firstJsonObject.getBoolean("success")
                        if (success) {

                            val data = firstJsonObject.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val ordrJsonObject = data.getJSONObject(i)
                                val orderObject = OrderItem(
                                    ordrJsonObject.getString("order_id"),
                                    ordrJsonObject.getString("restaurant_name"),
                                    ordrJsonObject.getString("total_cost"),
                                    ordrJsonObject.getString("order_placed_at"),
                                    ordrJsonObject.getJSONArray("food_items")
                                )
                                orderList.add(orderObject)
                                if (!orderList.isEmpty()) {
                                    rlOrderHistoryDisplay.visibility = View.VISIBLE
                                    llOrderhistorydefault.visibility = View.GONE

                                    layoutManager = LinearLayoutManager(activity as Context)
                                    recyclerAdapterUpper = OrderHistoryUpperRecyclerAdapter(
                                        activity as Context,
                                        orderList
                                    )
                                    recyclerOrderHistory.adapter = recyclerAdapterUpper
                                    recyclerOrderHistory.layoutManager = layoutManager

                                } else {
                                    rlOrderHistoryDisplay.visibility = View.GONE
                                    llOrderhistorydefault.visibility = View.VISIBLE
                                }
                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some error has occurred!!!",
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPreferences = context.getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )
    }
}