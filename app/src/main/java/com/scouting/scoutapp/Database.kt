package com.scouting.scoutapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class Database {
    private lateinit var bluetooth: BluetoothClass
    private val tag = "7G7 Bluetooth"
    private var robotMatchData: JSONArray = JSONArray()
    private var tempRobotMatchData: JSONObject = JSONObject()
    @SuppressLint("SdCardPath")
    private val filePath = "/data/data/com.scouting.scoutapp/files/teamData.json"

    // make a companion object to emulate a static field
    // TODO : sorta a hack I think?
    companion object {
        var teamData: JSONArray = JSONArray()
        var tempTeamData: JSONArray = JSONArray()
    }

    fun setup(bluetooth: BluetoothClass) {
        // init params
        this.bluetooth = bluetooth
        teamData = JSONArray()
        tempTeamData = JSONArray()
        this.robotMatchData = JSONArray()
        this.tempRobotMatchData = JSONObject()

        // Attempt to read from the current teamData.json
        try {
            val dat = readFile(filePath)
            Log.i(tag, "Data: '$dat'")
            teamData = JSONArray(dat)
        } catch (e: Exception) {
            when (e) {
                is FileNotFoundException, is JSONException -> {
                    Log.e(tag, "File not found! Trying again")
                    try {
                        // This bit creates a brand new file if one doesn't exist already
                        bluetooth.activity?.openFileOutput(filePath, Context.MODE_PRIVATE).use {
                            // NOTE: This may be unsafe. If there's a different JSON err for some reason, everything gets destroyed
                            // The question is whether there can be a JSON err for any reason other than "the file is empty"
                            Log.i(tag, "Writing to file")
                            if (it != null) it.write("{[]}".toByteArray()) else {
                                Log.e(tag, "it (bluetooth connection of FileOutputStream?) is null")
                                alert(
                                    this.bluetooth.activity!!,
                                    "FATAL CONNECTIVITY ERROR",
                                    "FATAL BLUETOOTH ERROR: Database.kt: `it` (bluetooth connection of type FileOutputStream?) is null. Please contact the scouting team to alert them of this error BEFORE exiting the app.",
                                    DialogInterface.OnClickListener {
                                        // TODO : recover?
                                        _, _ -> { }
                                    }
                                )
                            }
                        }

                        // retry
                        val dat = readFile(filePath)
                        Log.i(tag, "Data: '$dat'")
                        teamData = JSONArray(dat)
                    } catch (e1: JSONException) {
                        e1.printStackTrace()
                    }
                }
            }
        } catch (e: JSONException) {
            Log.e(tag, "Caught a JSON exception")
            e.printStackTrace()
        }
    }

    // Scrap a file
    // Mostly for testing
    fun scrap(name: String) {
        val file = File(name)
        file.delete()
    }

    // Read a file
    private fun readFile(name: String): String {
        return bluetooth.activity?.openFileInput(name)?.bufferedReader().use {
            if (it?.readText() == null) "" else it.readText()
        }
    }

    fun makeTeam(teamNumber: Int, teamName: String) {
        try {
            tempTeamData.put(JSONObject("{\"team\":${teamNumber},\"name\":${teamName}}"))
            this.send()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun send() {
        val data = "{\"matchData\":${robotMatchData},\"teamData\":${tempTeamData}}"
        bluetooth.send(data)
        Log.i(tag, "Sent data: $data")
    }

    fun dataSent(data: String) {
        try {
            teamData = JSONArray(data)
            bluetooth.activity?.openFileOutput("teamData.json", Context.MODE_PRIVATE)?.bufferedWriter().use {
                it?.write(teamData.toString())
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return
        } catch (e: JSONException) {
            e.printStackTrace()
            return
        }

        robotMatchData = JSONArray()
        tempTeamData = JSONArray()
    }

    fun createRobotMatch(teamNumber: Int, match: String) {
        try {
            // This is a minimal example of a JSONObject. In actual matches we can expand it
            tempRobotMatchData = JSONObject("{\"team\":\"$teamNumber\",\"matchID\":\"$match\"}")
        } catch (e: JSONException) {
            Log.e(tag, "Failed to create robot match")
            e.printStackTrace()
        }
    }

    fun finishMatch() {
        robotMatchData.put(tempRobotMatchData)
        tempRobotMatchData = JSONObject()
        this.send()
    }
}


//
//  util functions
//

// get the name of the team from the JSON db
fun getTeamName(i: Int): String {
    return Database.teamData.getJSONObject(i).getString("name")
}

// get the team number from the JSON db
fun getTeamNumber(i: Int): Int {
    return Database.teamData.getJSONObject(i).getInt("team")
}

// get local team name
fun getLocalTeamName(i: Int): String {
    return Database.tempTeamData.getJSONObject(i).getString("name")
}

// get local team number
fun getLocalTeamNumber(i: Int): Int {
    return Database.tempTeamData.getJSONObject(i).getInt("team")
}

// make a team
fun makeTeam(teamNumber: Int, teamName: String) {
    Database.tempTeamData.put(JSONObject("{\"team\":$teamNumber,\"name\":\"$teamName\"}"))
}