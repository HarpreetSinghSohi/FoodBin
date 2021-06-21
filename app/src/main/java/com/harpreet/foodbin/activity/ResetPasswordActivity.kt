package com.harpreet.foodbin.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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

class ResetPasswordActivity : AppCompatActivity() {

    lateinit var etOTP: EditText
    lateinit var etPasswordOne: EditText
    lateinit var etPasswordTwo: EditText
    lateinit var btnReset: Button
    lateinit var otp: String
    lateinit var passOne: String
    lateinit var passTwo: String

    var otpCheck = 0
    var passOneCheck = 0
    var passTwoCheck = 0

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        setContentView(R.layout.activity_reset_password)

        etOTP = findViewById(R.id.etResetOTP)
        etPasswordOne = findViewById(R.id.etResetPasswordOne)
        etPasswordTwo = findViewById(R.id.etResetPasswordTwo)
        btnReset = findViewById(R.id.btnReset)

        btnReset.setOnClickListener {

            fieldCheck()
            if (isAllValid()) {

                val mobile = intent.getStringExtra("mobile")
                val queue = Volley.newRequestQueue(this@ResetPasswordActivity)
                val url = "http://13.235.250.119/v2/reset_password/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mobile)
                jsonParams.put("password", passOne)
                jsonParams.put("otp", otp)
                if (ConnectionManager().checkConnectivity(this as Context)) {
                    val jsonObjectRequest =
                        object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                val message = data.getString("successMessage")
                                if (success) {
                                    sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                                    sharedPreferences.edit().putString("userId", "").apply()
                                    sharedPreferences.edit().putString("userName", "").apply()
                                    sharedPreferences.edit().putString("userEmail", "").apply()
                                    sharedPreferences.edit().putString("userMobile", "").apply()
                                    sharedPreferences.edit().putString("userAddress", "").apply()
                                    Toast.makeText(
                                        this@ResetPasswordActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(
                                        Intent(
                                            this@ResetPasswordActivity,
                                            LoginActivity::class.java
                                        )
                                    )
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@ResetPasswordActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@ResetPasswordActivity,
                                    "Some error has occurred !",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }, Response.ErrorListener {
                            Toast.makeText(
                                this@ResetPasswordActivity,
                                "Some error has occurred !",
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
                    this@ResetPasswordActivity,
                    "Something is not right",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun isAllValid(): Boolean {
        if (otpCheck == 1 && passOneCheck == 1 && passTwoCheck == 1) {
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
        passOne = etPasswordOne.text.toString()
        passTwo = etPasswordTwo.text.toString()
        otp = etOTP.text.toString()

        if (passOne == null || passOne.length < 4) {
            etPasswordOne.error = getString(R.string.password_length_error_message)
            passOneCheck = 0
        } else {
            passOneCheck = 1
        }
        if (passTwo == null || !passTwo.equals(passOne)) {
            etPasswordOne.error = getString(R.string.password_no_match_error_message)
            etPasswordTwo.error = getString(R.string.password_no_match_error_message)
            passTwoCheck = 0
        } else {
            passTwoCheck = 1
        }
        if (otp == null || otp.length != 4) {
            etOTP.error = getString(R.string.four_character_otp)
            otpCheck = 0
        } else {
            otpCheck = 1
        }
    }
}


