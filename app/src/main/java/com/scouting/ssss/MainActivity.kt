package com.scouting.ssss

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


// The beginning screen
class MainActivity : AppCompatActivity() {
    // Logging tag
    private val tag = "7G7 Bluetooth"
    private var bluetooth: BluetoothClass? = null
    private var started = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create buttons
        val newMatch = findViewById<Button>(R.id.newMatchBtn)

        // Set behavior for the button
        newMatch.setOnClickListener {
            Log.i(tag, "Clicked new match button")
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i(tag, "Started main activity")

        // bluetooth.setup
        if (!started) {
            bluetooth = BluetoothClass()
            // Send empty data to test the connection
            bluetooth?.send("{\"teamData\":[],\"matchData\":[]}".toByteArray())
            started = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(tag, "Destroyed the main activity")
        // bluetooth.end
    }
}
