package com.scouting.ssss

import android.content.Context
import android.util.Log
import bluetooth.Bluetooth
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.Exception

public class Database {
    private var bluetooth: BluetoothClass = BluetoothClass(null)
    private val tag = "7G7 Bluetooth"
    private var teamData: JSONArray = JSONArray()
    private var tempTeamData: JSONArray = JSONArray()
    private var robotMatchData: JSONArray = JSONArray()
    private var tempRobotMatchData: JSONObject = JSONObject()
    private val filePath = "/data/data/com.scouting.ssss/files/teamData.json"

    fun setup(bluetooth: BluetoothClass) {
        // init params
        this.bluetooth = bluetooth
        this.teamData = JSONArray()
        this.tempTeamData = JSONArray()
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
                            it?.write("{[]}".toByteArray())
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
            //e.printStackTrace()
        } catch (e: JSONException) {
            Log.e(tag, "Caught a JSON exception")
            e.printStackTrace()
        }
    }

    // Scrap a file
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
            tempTeamData.put( JSONObject("{\"team\":${teamNumber},\"name\":${teamName}}") )
            this.send()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun send() {
        val data = "{\"matchData\":${robotMatchData.toString()},\"teamData\":${tempTeamData.toString()}}"
        bluetooth.send(data)
        Log.i(tag, "Sent data: $data")
    }

    fun dataSent(data: String) {
        try {
            teamData = JSONArray(data)
            val fos = bluetooth.activity?.openFileOutput("teamData.json", Context.MODE_PRIVATE)?.bufferedWriter().use {
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