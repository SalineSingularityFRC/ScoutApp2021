package com.scouting.scoutapp

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class Teams : AppCompatActivity() {
    private val tag = "ScoutApp Teams activity"
    private lateinit var view: ListView

    // setup the elements
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(tag, "Started Teams activity")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teams)
        //setSupportActionBar(findViewById(R.id.toolbar))

        // get the view with its id in the layout
        this.view = findViewById(R.id.list_item)
    }

    override fun onResume() {
        // call AppCompatActivity.onResume(), which is different from our onResume()
        super.onResume()

        // list of items
        // TODO : get from pi
        val list = listOf<String>("a", "b", "c", "d", "e", "f", "g")

        // create an adapter attached to a layout, NOT a fragment or activity
        // must use the constructor that takes in a TextView (list_text in this instance)
        val arrayAdapter = ArrayAdapter<String>(this, R.layout.listlayout, R.id.list_text, list)

        view.adapter = arrayAdapter
    }
}