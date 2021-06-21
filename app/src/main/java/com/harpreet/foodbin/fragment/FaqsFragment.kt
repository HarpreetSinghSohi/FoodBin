package com.harpreet.foodbin.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.harpreet.foodbin.R
import com.harpreet.foodbin.adapter.FaqsRecyclerAdapter
import com.harpreet.foodbin.model.Faqs

class FaqsFragment : Fragment() {

    lateinit var recyclerFaqs: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FaqsRecyclerAdapter

    val faqsList = arrayListOf<Faqs>(
        Faqs(
            "Q.1 How will the food be delivered?",
            "A.1 Your food will be delivered by our delivery executive."
        ),
        Faqs(
            "Q.2 How long it will take to deliver my food?",
            "A.2 The time taken for the food to be delivered is mention in the order history section."
        ),
        Faqs(
            "Q.3 Is my food freshly prepared?",
            "A.3 That completely depend upon the restaurant and the type of food you ordered."
        ),
        Faqs(
            "Q.4 Can i schedule the delivery for my food?",
            "A.4 No you cannot schedule your food as the open and closing time of restaurants are not fix."
        ),
        Faqs(
            "Q.5 Can i get free food?",
            "A.5 Yes, you can only if the delivery executive delivers food after the estimated time of delivery."
        ),
        Faqs(
            "Q.6 How to rate my delivered food?",
            "A.6 That functionality is not yet introduced in this app. Sorry for your inconvenience."
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_faqs, container, false)

        recyclerFaqs = view.findViewById(R.id.recyclerFaqs)
        layoutManager = LinearLayoutManager(activity)
        recyclerAdapter = FaqsRecyclerAdapter(activity as Context, faqsList)
        recyclerFaqs.adapter = recyclerAdapter
        recyclerFaqs.layoutManager = layoutManager

        recyclerFaqs.addItemDecoration(
            DividerItemDecoration(
                recyclerFaqs.context,
                (layoutManager as LinearLayoutManager).orientation
            )
        )

        return view
    }
}