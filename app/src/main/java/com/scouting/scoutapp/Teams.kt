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

        val list = listOf<String>("a", "b", "c", "d", "e", "f", "g")
        val arrayAdapter = ArrayAdapter<String>(this, R.layout.listlayout, list)

        val view = findViewById<ListView>(R.id.list_item)
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