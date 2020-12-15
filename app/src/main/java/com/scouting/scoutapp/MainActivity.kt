package com.scouting.ssss

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// the version
// NOTE(@monarrk): this is set with a special compiler script (scripts/setvers.sh)
// this is ONLY usable on a unix (linux, mac, bsd, etc) computer with bourne shell (usually /bin/sh)
// or on windows with git bash or windows subsystem for linux
//
// this works by using sed to "s/{{VERSION}}/${VERSION}/g"
// where $VERSION is the content of version.txt and the current short git hash (`git rev-parse --short HEAD`)
//
// effectively this means any instance of the text {{VERSION}} in this file will be replaced with the version
// this allows me to increment/set the version easily with, say, every git commit so we know what's running on each tablet
//
// for more implementation details, the script is pretty well commented and should be ok to understand
//
// this was written by skye bleed and is largely magic
// if she is no longer working on this codebase, feel free to remove or modify it
const val VERSION = "{{VERSION}}"

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
