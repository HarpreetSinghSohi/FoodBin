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
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.harpreet.foodbin.R
import com.harpreet.foodbin.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_registration.*
import org.json.JSONException
import org.json.JSONObject

class RegistrationActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var etUseName: EditText
    lateinit var etUserEmail: EditText
    lateinit var etUserMobile: EditText
    lateinit var etUserAddress: EditText
    lateinit var etUserPassword: EditText
    lateinit var etUserConfirmPassword: EditText
    lateinit var btnRegister: Button

    lateinit var name: String
    lateinit var mobile: String
    lateinit var address: String
    lateinit var email: String
    lateinit var password: String
    lateinit var rePassWord: String
    var nameCheck = 0
    var mobileCheck = 0
    var emailCheck = 0
    var passOneCheck = 0
    var passTwoCheck = 0
    var addressCheck = 0

    lateinit var userId: String
    lateinit var userName: String
    lateinit var userMobile: String
    lateinit var userAddress: String
    lateinit var userEmail: String

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)


        setContentView(R.layout.activity_registration)

        initializeIds()
        setToolBar()

        btnRegister.setOnClickListener {

            fieldCheck()
            if (isAllValid()) {
                val queue = Volley.newRequestQueue(this@RegistrationActivity)
                val url = "http://13.235.250.119/v2/register/fetch_result"
                val jsonParam = JSONObject()
                jsonParam.put("name", name)
                jsonParam.put("mobile_number", mobile)
                jsonParam.put("password", password)
                jsonParam.put("address", address)
                jsonParam.put("email", email)
                if (ConnectionManager().checkConnectivity(this as Context)) {
                    val jsonRequest =
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

                                    Toast.makeText(
                                        this@RegistrationActivity,
                                        "Successfully registered.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    savePreferences()

                                    setResult(Activity.RESULT_OK, Intent())
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@RegistrationActivity,
                                        "Mobile number or email exists in database. Try other value",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "Some Error has occurred. in json object",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }, Response.ErrorListener {
                            Toast.makeText(
                                this@RegistrationActivity,
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
                    queue.add(jsonRequest)
                } else {
                    showDialog()
                }
            } else {
                Toast.makeText(
                    this@RegistrationActivity,
                    "Something is not right",
                    Toast.LENGTH_SHORT
                ).show()
            }

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
        toolbar = findViewById(R.id.toolbar)
        etUseName = findViewById(R.id.etUserName)
        etUserEmail = findViewById(R.id.etUserEmail)
        etUserMobile = findViewById(R.id.etUserMobileNumber)
        etUserAddress = findViewById(R.id.etUserAddress)
        etUserPassword = findViewById(R.id.etUserPassword)
        etUserConfirmPassword = findViewById(R.id.etUserConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
    }

    private fun setToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.register_yourself_toolbar_title)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun isAllValid(): Boolean {
        if (nameCheck == 1 && mobileCheck == 1 && emailCheck == 1 && addressCheck == 1 && passOneCheck == 1 && passTwoCheck == 1) {
            return true
        }
        return false
    }

    fun fieldCheck() {
        name = etUseName.text.toString()
        if (name == null || name.length < 3) {
            etUseName.error = getString(R.string.invalid_name_error_message)
            nameCheck = 0
        } else {
            nameCheck = 1
        }
        mobile = etUserMobile.text.toString()
        if (mobile == null || mobile.length < 10) {
            etUserMobile.error = getString(R.string.invalid_mobile_number_error_message)
            mobileCheck = 0
        } else {
            mobileCheck = 1
        }
        password = etUserPassword.text.toString()
        if (password == null || password.length < 4) {
            etUserPassword.error = getString(R.string.password_length_error_message)
            passOneCheck = 0
        } else {
            passOneCheck = 1
        }
        rePassWord = etUserConfirmPassword.text.toString()
        if (rePassWord == null || !rePassWord.equals(password)) {
            etUserPassword.error = getString(R.string.password_no_match_error_message)
            etUserConfirmPassword.error = getString(R.string.password_no_match_error_message)
            passTwoCheck = 0
        } else {
            passTwoCheck = 1
        }
        email = etUserEmail.text.toString()
        if (email == null || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etUserEmail.error = getString(R.string.invalid_email_error_message)
            emailCheck = 0
        } else {
            emailCheck = 1
        }
        address = etUserAddress.text.toString()
        if (address == null || address.length < 1) {
            etUserAddress.error = getString(R.string.empty_address_error_message)
            addressCheck = 0
        } else {
            addressCheck = 1
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
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