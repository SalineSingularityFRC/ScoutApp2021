package com.scouting.scoutapp

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class NewTeam : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_team)

        val createTeam = findViewById<Button>(R.id.createTeamButton)
        val teamName = findViewById<EditText>(R.id.teamNameEditText)
        val teamNumber = findViewById<EditText>(R.id.teamNumberEditText)

        createTeam.setOnClickListener {
            val checkTeamName = teamName.getText().toString()
            val checkTeamNumber = teamNumber.getText().toString()

            if (checkTeamName.matches(Regex(""))) {
                alert(this,
                    "Error",
                    "Please input a team name!",
                    DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                    // do nothing
                    })
            }

            if (checkTeamNumber.matches(Regex(""))) {
                alert(this,
                    "Error",
                    "Please input a team number!",
                    DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                    ; // do nothing
                    })
            }

            makeTeam(Integer.parseInt(checkTeamNumber), checkTeamName);
            finish()
        }
    }
}