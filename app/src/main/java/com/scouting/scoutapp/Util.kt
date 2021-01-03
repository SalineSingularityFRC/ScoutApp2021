package com.scouting.scoutapp

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

fun <T : AppCompatActivity> alert(obj: T, title: String, message: String, listener: DialogInterface.OnClickListener) {
    // make a popup message to alert the user
    val builder = AlertDialog.Builder(obj)
    builder.setMessage("FATAL ERROR: Teams.kt: view (type ListView?) is null. Please contact the scouting team to alert them of the error BEFORE you restart the app.")
        .setCancelable(false)
        // set the button so we can try to recover later
        .setPositiveButton("OK", listener)

    // create the alert from the builder we made
    val alert = builder.create()
    alert.setTitle(title)
    alert.show()
}