package com.scouting.scoutapp

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class Teams : AppCompatActivity() {
    val tag = "SSSS Teams activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(tag, "Started Teams activity")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teams)
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    override fun onResume() {
        super.onResume()

        // list
        // TODO : get from pi
        val list = listOf<String>("a", "b", "c", "d", "e", "f", "g")

        // create an adapter attached to a layout, NOT a fragment or activity
        val arrayAdapter = ArrayAdapter<String>(this, R.layout.listlayout, list)

        // get the view
        val view = findViewById<ListView>(R.id.list_item)
        // make sure view isn't null
        view?.let {
            it.adapter = arrayAdapter
        }

        /*if (view != null) {
            view.adapter = arrayAdapter
        } else {
            Log.e(tag, "View was null!")
        }*/
    }
}