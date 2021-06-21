package com.harpreet.foodbin.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.harpreet.foodbin.R
import com.harpreet.foodbin.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var etUserMobile: EditText
    lateinit var etUserEmail: EditText
    lateinit var btnNext: Button
    lateinit var mobile: String
    lateinit var email: String
    var mobileCheck = 0
    var emailCheck = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etUserMobile = findViewById(R.id.etForgotUserMobile)
        etUserEmail = findViewById(R.id.etForgotUserEmail)
        btnNext = findViewById(R.id.btnForgotNext)

        btnNext.setOnClickListener {

            fieldCheck()
            if (isAllValid()) {

                val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
                val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mobile)
                jsonParams.put("email", email)
                if (ConnectionManager().checkConnectivity(this as Context)) {
                    val jsonObjectRequest =
                        object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    val firstTry = data.getBoolean("first_try")
                                    val intent = Intent(
                                        this@ForgotPasswordActivity,
                                        ResetPasswordActivity::class.java
                                    )
                                    intent.putExtra("mobile", mobile)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        this@ForgotPasswordActivity,
                                        "Enter valid credentials.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "Some exception has occurred !",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }, Response.ErrorListener {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                it.networkResponse.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
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
            } else {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Something is not right",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun isAllValid(): Boolean {
        if (mobileCheck == 1 && emailCheck == 1) {
            return true
        }
        return false
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

    private fun fieldCheck() {
        mobile = etUserMobile.text.toString()
        if (mobile == null || mobile.length < 10) {
            etUserMobile.error = getString(R.string.invalid_mobile_number_error_message)
            mobileCheck = 0
        } else {
            mobileCheck = 1
        }
        email = etUserEmail.text.toString()
        if (email == null || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etUserEmail.error = getString(R.string.invalid_email_error_message)
            emailCheck = 0
        } else {
            emailCheck = 1
        }
    }
}