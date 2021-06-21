package com.harpreet.foodbin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.harpreet.foodbin.R
import com.harpreet.foodbin.model.Faqs

class FaqsRecyclerAdapter(val context: Context, val itemList: ArrayList<Faqs>) :
    RecyclerView.Adapter<FaqsRecyclerAdapter.FaqsViewHolder>() {

    class FaqsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvQuestion: TextView = view.findViewById(R.id.tvFaqsQuestion)
        val tvAnswer: TextView = view.findViewById(R.id.tvFaqsAnswers)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqsViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_faqs_single_row, parent, false)

        return FaqsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: FaqsViewHolder, position: Int) {

        val faqs = itemList[position]
        holder.tvQuestion.text = faqs.question
        holder.tvAnswer.text = faqs.answer
    }
}