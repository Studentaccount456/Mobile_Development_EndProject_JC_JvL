package com.example.mobile_dev_endproject_jc_jvl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ClubDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.club_details_screen)
    }

    private fun launchActivity(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }
}