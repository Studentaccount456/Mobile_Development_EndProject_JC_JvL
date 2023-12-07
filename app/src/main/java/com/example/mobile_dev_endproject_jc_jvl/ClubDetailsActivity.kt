package com.example.mobile_dev_endproject_jc_jvl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

private lateinit var clubNameText: TextView
private lateinit var addressText: TextView
private lateinit var locationText: TextView

class ClubDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.club_details_screen)

        val clubName = intent.getStringExtra("clubName")
        val clubAddress = intent.getStringExtra("clubAddress")
        val clubLocation = intent.getStringExtra("clubLocation")

        clubNameText = findViewById(R.id.clubNameText)
        addressText = findViewById(R.id.AdressText)
        locationText = findViewById(R.id.LocationText)

        clubNameText.text = clubName
        addressText.text = "ClubAddress: " + clubAddress
        locationText.text = "ClubLocation: " + clubLocation

    }

    private fun launchActivity(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }
}