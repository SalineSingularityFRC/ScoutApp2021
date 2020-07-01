package com.scouting.ssss

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.nio.charset.Charset


// The beginning screen
class MainActivity : AppCompatActivity() {
    // Logging tag
    private val tag = "7G7 Bluetooth"
    private var bluetooth: BluetoothClass? = null
    private var started = false
    var database: Database? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create buttons
        val newMatch = findViewById<Button>(R.id.newMatchBtn)

        // Set behavior for the button
        newMatch.setOnClickListener {
            Log.i(tag, "Clicked new match button")
        }

        Log.i(tag, "Setting up bluetooth")
        this.startBluetooth()
        if (!started) {
            val data = "{\"teamData\":[],\"matchData\":[]}"
            Log.i(tag, "Sending data '$data'")
            bluetooth?.send(data)
            started = true
        }

        Log.i(tag, "Setting up database")
        // TODO: Handle this better
        if (bluetooth == null) {
            Log.e(tag, "BLUETOOTH IS NULL, this is probably a bug")
            startBluetooth()
        }
        database = Database(bluetooth!!)
        database?.dataSent(bluetooth!!.currentData)
    }

    override fun onStart() {
        super.onStart()
        Log.i(tag, "Started main activity")

        if (bluetooth == null) startBluetooth()
    }

    private fun startBluetooth() {
        Log.i(tag, "Creating bluetooth instance")
        this.bluetooth = BluetoothClass(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(tag, "Destroyed the main activity")
        // bluetooth.end
    }
}
