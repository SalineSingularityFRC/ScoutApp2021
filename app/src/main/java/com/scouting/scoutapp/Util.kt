package com.scouting.scoutapp

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

// the version
// NOTE(@monarrk): this is set with a special compiler script (scripts/setvers.sh)
// this is ONLY usable on a unix (linux, mac, bsd, etc) computer with bourne shell (usually /bin/sh)
// or on windows with git bash or windows subsystem for linux
//
// this works by using sed to "s/{{VERSION}}/${VERSION}/g"
// where $VERSION is the content of version.txt and the current short git hash (`git rev-parse --short HEAD`)
// formatted as $VERSION-$HASH
//
// effectively this means any instance of the text {{VERSION}} in this file will be replaced with the version
// this allows me to increment/set the version easily with, say, every git commit so we know what's running on each tablet
//
// for more implementation details, the script is pretty well commented and should be ok to understand
//
// this was written by skye bleed and is largely magic
// if she is no longer working on this codebase, feel free to remove or modify it
const val VERSION = "{{VERSION}}"

fun <T : AppCompatActivity> alert(obj: T, title: String, message: String, listener: DialogInterface.OnClickListener) {
    // make a popup message to alert the user
    val builder = AlertDialog.Builder(obj)
    builder.setMessage(message)
        .setCancelable(false)
        // set the button so we can try to recover later
        .setPositiveButton("OK", listener)

    // create the alert from the builder we made
    val alert = builder.create()
    alert.setTitle(title)
    alert.show()
}
