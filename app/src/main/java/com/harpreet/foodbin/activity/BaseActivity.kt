package com.harpreet.foodbin.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.harpreet.foodbin.*
import com.harpreet.foodbin.fragment.*
import com.harpreet.foodbin.util.ConnectionManager

class BaseActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var headerView: View
    lateinit var tvHeaderUserName: TextView
    lateinit var tvHeaderUserMobile: TextView

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_base)



        initializeIds()
        setUpToolBar()
        setHeader()
        openHome()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@BaseActivity, drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )



        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        //To set the action on the items in the navigation drawer
        navigationView.setNavigationItemSelectedListener {

            headerView.setOnClickListener {
                supportFragmentManager.beginTransaction().replace(
                    R.id.frame,
                    ProfileFragment()
                ).commit()
                supportActionBar?.title = getString(R.string.profile)
                drawerLayout.closeDrawers()
            }

            when (it.itemId) {
                R.id.home -> {
                    openHome()
                    drawerLayout.closeDrawers()
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame,
                        ProfileFragment()
                    ).commit()
                    supportActionBar?.title = getString(R.string.profile)
                    drawerLayout.closeDrawers()
                }
                R.id.favourite -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame,
                        FavouriteFragment()
                    ).commit()
                    supportActionBar?.title = getString(R.string.favorites)
                    drawerLayout.closeDrawers()
                }
                R.id.orderHistory -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame,
                        OrderHistoryFragment()
                    ).commit()
                    supportActionBar?.title = getString(R.string.order_history)
                    drawerLayout.closeDrawers()
                }
                R.id.faqs -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame,
                        FaqsFragment()
                    ).commit()
                    supportActionBar?.title = getString(R.string.faqs)
                    drawerLayout.closeDrawers()
                }
                R.id.logout -> {
                    drawerLayout.closeDrawers()
                    val dialog = AlertDialog.Builder(this@BaseActivity)
                    dialog.setTitle(R.string.confirmation)
                    dialog.setMessage(R.string.logout_message)
                    dialog.setPositiveButton(getString(R.string.yes)) { text, listener ->
                        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                        sharedPreferences.edit().putString("userId", "").apply()
                        sharedPreferences.edit().putString("userName", "").apply()
                        sharedPreferences.edit().putString("userEmail", "").apply()
                        sharedPreferences.edit().putString("userMobile", "").apply()
                        sharedPreferences.edit().putString("userAddress", "").apply()
                        startActivity(Intent(this@BaseActivity, LoginActivity::class.java))
                        finish()

                    }
                    dialog.setNegativeButton(getString(R.string.no)) { text, listener ->

                    }
                    dialog.create()
                    dialog.show()

                }
            }

            return@setNavigationItemSelectedListener true
        }
    }

    private fun setHeader() {
        val name = sharedPreferences.getString("userName", "User")
        val mobile = sharedPreferences.getString("userMobile", "0000000000")
        tvHeaderUserName.text = name
        tvHeaderUserMobile.text = mobile
    }


    private fun initializeIds() {
        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)
        headerView = navigationView.getHeaderView(0)
        tvHeaderUserName = headerView.findViewById(R.id.tvHeaderUserName)
        tvHeaderUserMobile = headerView.findViewById(R.id.tvHeaderUserMobile)
    }

    private fun setUpToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun openHome() {
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
        supportActionBar?.title = getString(R.string.all_restaurants)
        navigationView.setCheckedItem(R.id.home)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)
        when (frag) {
            !is HomeFragment -> {
                openHome()
                drawerLayout.closeDrawers()
            }
            else -> super.onBackPressed()
        }
    }
}