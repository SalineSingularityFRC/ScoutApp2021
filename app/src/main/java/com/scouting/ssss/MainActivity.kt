package com.scouting.ssss

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

const val VERSION = "0.1.0"

// The beginning screen
class MainActivity : AppCompatActivity() {
    // Logging tag
    private val tag = "7G7 Bluetooth"
    private var bluetooth: BluetoothClass = BluetoothClass(this)
    private var started = false
    var database: Database = Database()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create buttons
        val newMatch = findViewById<Button>(R.id.newMatchBtn)

        // Set behavior for the button
        newMatch.setOnClickListener {
            Log.i(tag, "Clicked new match button")
        }

        // Display the version number
        val version = findViewById<TextView>(R.id.version)
        version.text = "Version $VERSION"

        Log.i(tag, "Setting up database")
        database.setup(bluetooth)
    }

    override fun onStart() {
        super.onStart()
        Log.i(tag, "Started main activity")

        if (!started) {
            Log.i(tag, "Setting up bluetooth")
            bluetooth.setup()
            val data = "{\"teamData\":[],\"matchData\":[]}"
            Log.i(tag, "Sending data '$data'")
            bluetooth.send(data)
            started = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(tag, "Destroyed the main activity")
        bluetooth.end()
    }
}
