package com.example.geoalarm

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class RecyclerHomeAdapter(private val context : Context) : RecyclerView.Adapter<RecyclerHomeAdapter.ViewHolder>() {


    private val db = DatabaseHandler(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recyclerview , parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.showmessage.text = itemList[position].message
        val lati = itemList[position].latitude
        val longi = itemList[position].longitude
        val message = itemList[position].message
        holder.showlatitude.text = ((lati * 100.0).roundToInt()/100.0).toString()
        holder.showlongitude.text = ((longi * 100.0).roundToInt()/100.0).toString()
        holder.delbtn.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete")
                .setMessage("Are you sure want to delete this alarm?")
                .setCancelable(true)
                .setPositiveButton("Yes"){dialogInterface,_ ->
                    dialogInterface.dismiss()
                    Toast.makeText(context, "Message deleted", Toast.LENGTH_LONG).show()
                    itemList.removeAt(position)
                    notifyItemRemoved(position)
                    db.deletefromDB(message,lati,longi)
                }
                .setNegativeButton("No"){dialogInterface,_ ->
                    dialogInterface.cancel()
                }
                .show()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var showmessage: TextView = itemView.findViewById(R.id.showmessage)
        val showlatitude: TextView = itemView.findViewById(R.id.showlatitude)
        var showlongitude: TextView = itemView.findViewById(R.id.showlongitude)
        var delbtn: ImageButton = itemView.findViewById(R.id.delentrybtn)
    }
}