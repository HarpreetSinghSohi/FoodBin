package com.harpreet.foodbin.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.harpreet.foodbin.R
import com.harpreet.foodbin.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    lateinit var etUserMobile: EditText
    lateinit var etUserPassword: EditText
    lateinit var tvForgotPassword: TextView
    lateinit var tvRegisterUser: TextView
    lateinit var btnLogin: Button
    lateinit var sharedPreferences: SharedPreferences

    lateinit var password: String
    lateinit var mobile: String
    var mobileCheck = 0
    var passwordCheck = 0
    lateinit var userId: String
    lateinit var userName: String
    lateinit var userEmail: String
    lateinit var userMobile: String
    lateinit var userAddress: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        setContentView(R.layout.activity_login)

        initialiseIds()

        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            startHomeActivity()
        }


        btnLogin.setOnClickListener {

            fieldCheck()
            if (isAllValid()) {
                val queue = Volley.newRequestQueue(this@LoginActivity)
                val url = "http://13.235.250.119/v2/login/fetch_result"
                val jsonParam = JSONObject()
                jsonParam.put("mobile_number", mobile)
                jsonParam.put("password", password)
                if (ConnectionManager().checkConnectivity(this as Context)) {
                    val jsonObjectRequest =
                        object : JsonObjectRequest(Method.POST, url, jsonParam, Response.Listener {

                            try {
                                val jsonObjectFirst = it.getJSONObject("data")
                                val success = jsonObjectFirst.getBoolean("success")
                                if (success) {
                                    val userJsonObject = jsonObjectFirst.getJSONObject("data")
                                    userId = userJsonObject.getString("user_id")
                                    userName = userJsonObject.getString("name")
                                    userEmail = userJsonObject.getString("email")
                                    userMobile = userJsonObject.getString("mobile_number")
                                    userAddress = userJsonObject.getString("address")

                                    savePreferences()
                                    startHomeActivity()
                                } else {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Enter valid credentials",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Some Error has occurred. in json object",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                            Response.ErrorListener {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Some Error Occurred",
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
                Toast.makeText(this@LoginActivity, "Something is not right", Toast.LENGTH_SHORT)
                    .show()
            }

        }

        tvRegisterUser.setOnClickListener {
            startActivityForResult(Intent(this@LoginActivity, RegistrationActivity::class.java), 1)
        }

        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            startHomeActivity()
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

    private fun isAllValid(): Boolean {
        if (mobileCheck == 1 && passwordCheck == 1) {
            return true
        }
        return false
    }

    private fun fieldCheck() {
        mobile = etUserMobile.text.toString()
        if (mobile.isEmpty() || mobile.length < 10) {
            etUserMobile.error = getString(R.string.invalid_mobile_number_error_message)
            mobileCheck = 0
        } else {
            mobileCheck = 1
        }
        password = etUserPassword.text.toString()
        if (password.isEmpty() || password.length < 4) {
            etUserPassword.error = getString(R.string.password_length_error_message)
            passwordCheck = 0
        } else {
            passwordCheck = 1
        }
    }

    private fun initialiseIds() {
        etUserMobile = findViewById(R.id.etUserMobile)
        etUserPassword = findViewById(R.id.etUserPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        tvRegisterUser = findViewById(R.id.tvRegisterUser)
    }

    private fun startHomeActivity() {
        startActivity(Intent(this@LoginActivity, BaseActivity::class.java))
        finish()
    }

    fun savePreferences() {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        sharedPreferences.edit().putString("userId", userId).apply()
        sharedPreferences.edit().putString("userName", userName).apply()
        sharedPreferences.edit().putString("userEmail", userEmail).apply()
        sharedPreferences.edit().putString("userMobile", userMobile).apply()
        sharedPreferences.edit().putString("userAddress", userAddress).apply()
    }
}