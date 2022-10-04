package com.example.myproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this,MapsActivity::class.java)
        val fn = findViewById<FloatingActionButton>(R.id.add)
        fn.setOnClickListener {
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.opt,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId:Int = item.itemId
        if(itemId == R.id.helpbutton){
            val intent = Intent(this,helpactivity::class.java)
            startActivity(intent)
        }
        else if (itemId == R.id.contactbutton){
            val intent = Intent(this,contactactivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}