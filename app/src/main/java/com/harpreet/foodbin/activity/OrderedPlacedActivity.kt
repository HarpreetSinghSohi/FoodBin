package com.harpreet.foodbin.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.harpreet.foodbin.R

class OrderedPlacedActivity : AppCompatActivity() {

    lateinit var btnOk: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ordered_placed)

        btnOk = findViewById(R.id.btnOk)

        btnOk.setOnClickListener {
            finish()
        }

    }
}