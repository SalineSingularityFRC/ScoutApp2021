package com.scouting.scoutapp

import android.content.DialogInterface
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
        setSupportActionBar(findViewById(R.id.toolbar))

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
        val arrayAdapter = ArrayAdapter<String>(this, R.layout.listlayout, R.id.list_text, list)

        view.adapter = arrayAdapter

        // make sure view isn't null
        // this if pattern unwraps the ListView? into a non-optional type (ListView)
        /*if (view != null) {
            // set the adapter to the list
            view.adapter = arrayAdapter
        } else {
            // let our users know and exit the app
            Log.e(tag, "View was null!")

            // make a popup message to alert the user
            // the "alert" function is defined in Util.kt
            alert(
                this,
                "FATAL LAYOUT ERROR!",
                "FATAL ERROR: Teams.kt: view (type ListView?) is null. Please contact the scouting team to alert them of the error BEFORE you restart the app.",
                DialogInterface.OnClickListener {
                    // TODO: attempt to recover?
                    // dialog, id
                    _, _ -> Log.i(tag, "Attempting to recover...")
                })
        }*/
    }
}