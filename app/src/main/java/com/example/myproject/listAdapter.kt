package com.example.myproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class listAdapter(var listview : ArrayList<String>):RecyclerView.Adapter<listAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listrecyclerview,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.message.text = listview[position]
    }

    override fun getItemCount(): Int {
        return listview.size
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        var message = view.findViewById<TextView>(R.id.listmessage)
    }

}