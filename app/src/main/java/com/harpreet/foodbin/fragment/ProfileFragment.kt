package com.harpreet.foodbin.fragment

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.harpreet.foodbin.R

class ProfileFragment : Fragment() {

    lateinit var tvUserName: TextView
    lateinit var tvUserMobile: TextView
    lateinit var tvUserEmail: TextView
    lateinit var tvUserAddress: TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        tvUserName = view.findViewById(R.id.tvProfileUserName)
        tvUserAddress = view.findViewById(R.id.tvProfileUserAddress)
        tvUserMobile = view.findViewById(R.id.tvProfileUserMobile)
        tvUserEmail = view.findViewById(R.id.tvProfileUserEmail)

        val name = sharedPreferences.getString("userName", " ")
        val mobile = sharedPreferences.getString("userMobile", " ")
        val email = sharedPreferences.getString("userEmail", " ")
        val address = sharedPreferences.getString("userAddress", " ")

        tvUserName.text = name
        tvUserMobile.text = mobile
        tvUserEmail.text = email
        tvUserAddress.text = address

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPreferences = context.getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )
    }
}