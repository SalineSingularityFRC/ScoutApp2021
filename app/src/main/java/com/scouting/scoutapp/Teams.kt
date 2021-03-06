package com.scouting.scoutapp

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.*
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
        this.view.setOnItemClickListener { adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            //val teamNumber = this.view.findViewById<TextView>(R.id.)
            // start MatchInformation page
            Log.i(tag, "Clicked the team view")
        }

        val newTeam = findViewById<Button>(R.id.newTeamButton)
        newTeam.setOnClickListener {
            // start NewTeam page
            Log.i(tag, "Clicked 'New Team' button")

            val newTeam = Intent(applicationContext, NewTeam::class.java)
            startActivity(newTeam)
        }
    }

    override fun onResume() {
        // call AppCompatActivity.onResume(), which is different from our onResume()
        super.onResume()

        // list of items
        val list = mutableListOf<HashMap<String, String>>()

        // create an adapter attached to a layout, NOT a fragment or activity
        // the adapter takes a list of hashmaps and presents them in a friendly way on the app
        // must use the constructor that takes in a TextView (list_text in this instance)
        val arrayAdapter = SimpleAdapter(this,
            list,
            R.layout.listlayout,
            arrayOf("name", "number"), // the keys to use from the map
            intArrayOf(R.id.teamName, R.id.teamNumber)) // the ids on the listlayout to map to the values

        view.adapter = arrayAdapter

        // bluetooth code

        // iterate over all teams in the database
        // for (int i = 0; i < teamData.length(); i++)
        for (i in 0 until Database.teamData.length()) {
            val resultsMap = HashMap<String, String>()

            // set name and number
            resultsMap["name"] = getTeamName(i)
            resultsMap["number"] = "${getTeamNumber(i)}"
            list.add(resultsMap)
        }

        // iterate over local teams
        for (i in 0 until Database.tempTeamData.length()) {
            val resultsMap = HashMap<String, String>()

            resultsMap["name"] = getLocalTeamName(i)
            resultsMap["number"] = "${getLocalTeamNumber(i)}"
            list.add(resultsMap)
        }

        Log.i(tag, "list: $list")

        arrayAdapter.notifyDataSetChanged()
    }
}